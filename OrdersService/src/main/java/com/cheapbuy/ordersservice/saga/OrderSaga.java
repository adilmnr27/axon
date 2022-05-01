package com.cheapbuy.ordersservice.saga;

import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cheapbuy.core.commands.ReserveProductCommand;
import com.cheapbuy.core.events.ProductReservedEvent;
import com.cheapbuy.ordersservice.command.OrderAggregate;
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
		SagaLifecycle.end();
	}
}
