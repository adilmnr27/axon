package com.cheapbuy.ordersservice.saga;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cheapbuy.core.commands.CancelProductReservationCommand;
import com.cheapbuy.core.commands.ProcessPaymentCommand;
import com.cheapbuy.core.commands.ReserveProductCommand;
import com.cheapbuy.core.events.PaymentProcessedEvent;
import com.cheapbuy.core.events.ProductReservationCancelledEvent;
import com.cheapbuy.core.events.ProductReservedEvent;
import com.cheapbuy.core.model.User;
import com.cheapbuy.core.query.FetchUserPaymentDetailsQuery;
import com.cheapbuy.ordersservice.command.ApproveOrderCommand;
import com.cheapbuy.ordersservice.command.RejectOrderCommand;
import com.cheapbuy.ordersservice.core.data.OrderRestModel;
import com.cheapbuy.ordersservice.core.data.OrderSummary;
import com.cheapbuy.ordersservice.core.events.OrderApprovedEvent;
import com.cheapbuy.ordersservice.core.events.OrderCreatedEvent;
import com.cheapbuy.ordersservice.core.events.OrderRejectedEvent;
import com.cheapbuy.ordersservice.query.FindOrderQuery;

/**
 * Orchestration Saga Design Pattern.<br>
 * Saga will publish command and handle events<br>
 * Compensating transactions are performed in reverse order<br>
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
	
	@Autowired
	private transient DeadlineManager deadlineManager;
	
	@Autowired
	private transient QueryUpdateEmitter queryUpdateEmitter; //To handle subscriptionQuery
	
	private String deadlineId; //TODO: need to check for thread safety
	
	private static final String PAYMENT_PROCESSING_DEADLINE = "payment-processing-deadline";

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
		
		//For subscription Query
//		queryUpdateEmitter.emit(FindOrderQuery.class, query -> true,
//				new OrderSummary(orderCreatedEvent.getOrderId(), orderCreatedEvent.getOrderStatus()));
		
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
					RejectOrderCommand rejectOrderCommand = RejectOrderCommand.builder()
							.orderId(reserveProductCommand.getOrderId())
							.cancellationReason(commandResultMessage.exceptionResult().getMessage())
							.build();
					commandGateway.send(rejectOrderCommand);
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
			handle(productReservedEvent,e.getMessage());
			return;
		}
		
		if(user==null || user.getPaymentDetails()==null) {
			LOGGER.error("User and payment details received from UsersService is null");
			//start compensating transactions
			handle(productReservedEvent,"User Payment Details were null");
			return;
		}
		
		LOGGER.info("User and its payment info successfully received. Starting with payment Processing. Details {}", user);
		
		
		// scheduling a deadline
		deadlineId = deadlineManager.schedule(Duration.of(10, ChronoUnit.SECONDS), PAYMENT_PROCESSING_DEADLINE,
				productReservedEvent);

		//For triggering above deadline, please uncomment the below line
		//if(true) {return;}
		
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
			handle(productReservedEvent,e.getMessage());
			return;
		}
		
		if(result == null) {  //result is the paymentid
			LOGGER.error("Result is null while processing payment for orderid {} and paymentid {}", paymentCommand.getOrderId(), paymentCommand.getPaymentId());
			//Start Compensating transactions
			handle(productReservedEvent,"Result is null while processing payment for orderid " + paymentCommand.getOrderId() + " and paymentid " + paymentCommand.getPaymentId());
			return;
		}
	
	}
	
	/**
	 * Raise command to Cancel Product reservation
	 * @param productReservedEvent The event which did had problems
	 * @param reasonForCancellation The reason why the product reserved event failed
	 */
	private void handle(ProductReservedEvent productReservedEvent, String reasonForCancellation) {
		
		//cancelling the payment processing deadline as we are already starting compenstating transaction
		cancelPaymentProcessingDeadline();
				
		CancelProductReservationCommand cancelProductReservationCommand = CancelProductReservationCommand.builder()
				.productId(productReservedEvent.getProductId())
				.orderId(productReservedEvent.getOrderId())
				.quantity(productReservedEvent.getQuantity())
				.userId(productReservedEvent.getUserId())
				.cancellationReason(reasonForCancellation)
				.build();
		
		commandGateway.send(cancelProductReservationCommand);
	}
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(PaymentProcessedEvent paymentProcessedEvent) {
		LOGGER.info("Payment Processing succesfully completed for orderid {} and paymentid {}",
				paymentProcessedEvent.getOrderId(), paymentProcessedEvent.getPaymentId());
		
		//cancelling the payment processing deadline as payment has been processed
		cancelPaymentProcessingDeadline();
				
		ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());
		commandGateway.send(approveOrderCommand);
		
	}
	
	private void cancelPaymentProcessingDeadline() {
		if(deadlineId!=null) {
			LOGGER.info("Cancelling cancelPaymentProcessingDeadline");
			deadlineManager.cancelSchedule(PAYMENT_PROCESSING_DEADLINE,deadlineId);
			//deadlineManager.cancelAll(PAYMENT_PROCESSING_DEADLINE);
		 deadlineId=null;
		}
		
	}
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderApprovedEvent orderApprovedEvent) {
		LOGGER.info("Order Approved succesfully for orderid {} ",
				orderApprovedEvent.getOrderId());

		//For subscription Query
		queryUpdateEmitter.emit(FindOrderQuery.class, query -> true,
				new OrderSummary(orderApprovedEvent.getOrderId(), orderApprovedEvent.getOrderStatus()));
		
		SagaLifecycle.end();
	}
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(ProductReservationCancelledEvent productReservationCancelledEvent) {
		LOGGER.info("OrderSaga: Inside ProductReservationCancelledEvent for orderid {} . Proceeding to start compensating transaction to Reject Order",
				productReservationCancelledEvent.getOrderId());
		RejectOrderCommand rejectOrderCommand = RejectOrderCommand.builder()
				.orderId(productReservationCancelledEvent.getOrderId())
				.cancellationReason(productReservationCancelledEvent.getCancellationReason())
				.build();
		commandGateway.send(rejectOrderCommand);
	}
	
	@SagaEventHandler(associationProperty = "orderId")
	@EndSaga
	public void handle(OrderRejectedEvent orderRejectedEvent) {
		LOGGER.info("OrderSaga: Order Successfully Rejected for orderid {} ",
				orderRejectedEvent.getOrderId());
		
		//For subscription Query
		queryUpdateEmitter.emit(FindOrderQuery.class, query -> true,
				new OrderSummary(orderRejectedEvent.getOrderId(), orderRejectedEvent.getOrderStatus()));
	}
	
	/**
	 * 
	 * @param productReservedEvent Optional Payload sent while scheduling deadline
	 */
	@DeadlineHandler(deadlineName = PAYMENT_PROCESSING_DEADLINE) // stating that this method handles deadline events
	public void handlePaymentDeadline(ProductReservedEvent productReservedEvent) {
		LOGGER.info(" PAYMENT_PROCESSING_DEADLINE took place. Proceding to start compensating transaction");
		handle(productReservedEvent, "Payment Processing was not completed before the deadline for order id "
				+ productReservedEvent.getOrderId() + ". Starting compensating transactions");

	}
}
