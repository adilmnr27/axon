package com.cheapbuy.ordersservice.command;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cheapbuy.ordersservice.core.data.OrderStatus;
import com.cheapbuy.ordersservice.core.events.OrderCreatedEvent;

@Aggregate // Axon framework will know its an Aggregate class
public class OrderAggregate {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderAggregate.class);

	/**
	 * Will be used to initialize the current state of the Aggregate with the latest
	 * information.
	 * 
	 */
	@AggregateIdentifier // will help to axon framework link createOrderCommand with Aggregate
	public String orderId;
	
	private String userId;
	private String productId;
	private Integer quantity;
	private String addressId;
	private OrderStatus orderStatus;

	/*
	 * Default constructor required by axon framework
	 */
	public OrderAggregate() {
		LOGGER.info("Inside default constructor of OrderAggregate");

	}

	@CommandHandler
	public OrderAggregate(CreateOrderCommand createOrderCommand) {

		LOGGER.info("OrderAggregate: Inside Command Handler");
		var orderCreatedEvent = OrderCreatedEvent.builder().orderId(createOrderCommand.getOrderId())
				.productId(createOrderCommand.getProductId()).userId(createOrderCommand.getUserId())
				.quantity(createOrderCommand.getQuantity()).addressId(createOrderCommand.getAddressId())
				.orderStatus(createOrderCommand.getOrderStatus()).build();

		// Aggregate will publish the the event with Apply method from
		// AggregateLifeCycle.
		// As soon as apply method is called, it will dispatch the event to all event
		// handlers (here know as event source handler) inside this aggregate, so that
		// state of Aggregate can be updated with new information.
		// Applying events means they are immediately applied(published) to the
		// aggregate and scheduled for publication to other event handlers.
		LOGGER.info("OrderAggregate: Dispatching orderCreatedEvent");
		AggregateLifecycle.apply(orderCreatedEvent);

	}

	/**
	 * Naming convention is very short for event sourcing methods. <br>
	 * Just "on" Handles the event raised by our aggregate. (Events are dispatched
	 * by calling the apply method) <br>
	 * Will be used to initialize the current state of the Aggregate with the latest
	 * information. <br>
	 * Hence we would require instance variables related to product information.
	 * <br>
	 * Should avoid business logic and only use it to update Aggregate state <br>
	 * 
	 * orderCreatedEvent after this method will be scheduled for publication to
	 * other event handler and then persisted in event store <br>
	 */
	@EventSourcingHandler
	public void on(OrderCreatedEvent orderCreatedEvent) {
		LOGGER.info("Inside Event Source Handler for OrderCreatedEvent ");
		this.addressId = orderCreatedEvent.getAddressId();
		this.orderId = orderCreatedEvent.getOrderId();
		this.orderStatus = orderCreatedEvent.getOrderStatus();
		this.productId = orderCreatedEvent.getProductId();
		this.userId = orderCreatedEvent.getUserId();
		this.quantity = orderCreatedEvent.getQuantity();

	}

}
