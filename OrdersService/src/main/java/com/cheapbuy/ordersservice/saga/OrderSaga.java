package com.cheapbuy.ordersservice.saga;

import java.util.UUID;

import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cheapbuy.core.commands.ProcessPaymentCommand;
import com.cheapbuy.core.commands.ReserveProductCommand;
import com.cheapbuy.core.events.PaymentProcessedEvent;
import com.cheapbuy.core.events.ProductReservedEvent;
import com.cheapbuy.core.model.User;
import com.cheapbuy.core.query.FetchUserPaymentDetailsQuery;
import com.cheapbuy.ordersservice.command.ApproveOrderCommand;
import com.cheapbuy.ordersservice.command.OrderAggregate;
import com.cheapbuy.ordersservice.core.events.OrderApprovedEvent;
import com.cheapbuy.ordersservice.core.events.OrderCreatedEvent;

/**
 * Orchestration Saga Design Pattern.<br>
 * Saga will publish command and handle events<br>
 *
 */
@Saga // Annotation that informs Axon's auto configurer for Spring that a given
		// Component is a saga instance.
public class OrderSaga {

	/**
	 * Since Saga is Serializable, its important to mark the component we are
	 * injecting @transient
	 */
	@Autowired
	private transient CommandGateway commandGateway;
	
	@Autowired
	private transient QueryGateway queryGateway;

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

//	public OrderSaga(CommandGateway commandGateway) {
//		this.commandGateway = commandGateway;
//	}
	
	public OrderSaga() {
		
	}

	@StartSaga
	// associaionProperty: The property in the event that will provide the value to
	// find the Saga
	// instance. Typically, this value is a aggregate identifier of an aggregate
	// that a specific saga monitors.
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderCreatedEvent orderCreatedEvent) {
		
		LOGGER.info("Orders Saga: Handling OrderCreatedEvent");
		ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
				.orderId(orderCreatedEvent.getOrderId())
				.productId(orderCreatedEvent.getProductId())
				.quantity(orderCreatedEvent.getQuantity())
				.userId(orderCreatedEvent.getUserId())
				.build();

		this.commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {

			@Override
			public void onResult(CommandMessage<? extends ReserveProductCommand> commandMessage,
					CommandResultMessage<? extends Object> commandResultMessage) {
				if (commandResultMessage.isExceptional()) {
					
					LOGGER.error("Error Occured while in publishing product command flow. Exception: {}",
							commandResultMessage.exceptionResult().getMessage());
					
					//Start a compensating transaction
				}

			}

		});
	}
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(ProductReservedEvent productReservedEvent) {
		LOGGER.info("Inside Handling of  Product Reserved Event in OrdersSaga");
		//Process User Payment
		
		FetchUserPaymentDetailsQuery  fetchUserPaymentDetailsQuery = new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());
		User user = null;
		try {
			user=	queryGateway.query(fetchUserPaymentDetailsQuery, User.class).join();
		} catch (Exception e) {
			LOGGER.error("Error in fetch payment details from UsersService. Exception: {}", e.getMessage());
			//start compensating transactions
		}
		
		if(user==null) {
			LOGGER.error("User and payment details received from UsersService is null");
			//start compensating transactions
		}
		
		LOGGER.info("User and its payment info successfully received. Starting with payment Processing. Details {}", user);
		
		
		//Dipatching PaymentProcessComand
		
		ProcessPaymentCommand paymentCommand = ProcessPaymentCommand.builder()
				.orderId(productReservedEvent.getOrderId())
				.paymentDetails(user.getPaymentDetails())
				.paymentId(UUID.randomUUID().toString())
				.build();
		String result = null;
		try {
			result = commandGateway.sendAndWait(paymentCommand);
		} catch (Exception e) {
			LOGGER.error("Unable to Process payment for orderid {} and paymentid {}. Exception: {}",
					paymentCommand.getOrderId(), paymentCommand.getPaymentId(), e.getMessage());
			//Start Compensating transactions
		}
		
		if(result == null) {  //result is the paymentid
			LOGGER.error("Result is null while processing payment for orderid {} and paymentid {}", paymentCommand.getOrderId(), paymentCommand.getPaymentId());
			//Start Compensating transactions
		}
	
	}
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(PaymentProcessedEvent paymentProcessedEvent) {
		LOGGER.info("Payment Processing succesfully completed for orderid {} and paymentid {}",
				paymentProcessedEvent.getOrderId(), paymentProcessedEvent.getPaymentId());
		
		ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());
		commandGateway.send(approveOrderCommand);
		
	}
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderApprovedEvent orderApprovedEvent) {
		LOGGER.info("Order Approved succesfully for orderid {} ",
				orderApprovedEvent.getOrderId());
		
		SagaLifecycle.end();
	}
}
