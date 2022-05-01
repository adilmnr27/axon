package com.cheapbuy.ProductsService.command;


import java.math.BigDecimal;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.cheapbuy.ProductsService.core.events.ProductCreatedEvent;
import com.cheapbuy.core.commands.ReserveProductCommand;
import com.cheapbuy.core.events.ProductReservedEvent;

@Aggregate // Axon framework will know its an Aggregate class
public class ProductAggregate {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductAggregate.class);

	/**
	 * Will be used to initialize the current state of the Aggregate with the latest
	 * information.
	 * 
	 */
	@AggregateIdentifier // will help to axon framework link createProductCommand with Aggregate
	private String productId;
	
	private String title;
	private BigDecimal price;
	private Integer quantity;

	/*
	 * Default constructor required by axon framework
	 */
	public ProductAggregate() {
		LOGGER.info("Inside default constructor of Product Aggregate");
	}

	/**
	 * When CreateProductCommand is dispatched, this handler will be invoked
	 * 
	 * @param createProductCommand The dispatched command which will invoke this
	 *                             function
	 */
	@CommandHandler
	public ProductAggregate(CreateProductCommand createProductCommand) {

		LOGGER.info("Inside Command Handler for CreateProductCommand");

		// validate product command
		if (createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Product Price cannot be zero or less than zero");
		}

		if (!StringUtils.hasText(createProductCommand.getTitle())) {
			throw new IllegalArgumentException("Product Title cannot be empty");
		}

		ProductCreatedEvent productCreatedEvent = ProductCreatedEvent.builder().price(createProductCommand.getPrice())
				.title(createProductCommand.getTitle()).quantity(createProductCommand.getQuantity())
				.productId(createProductCommand.getProductId()).build();

		// Aggregate will publish the the event with Apply method from AggregateLifeCycle.
		// As soon as apply method is called, it will dispatch the event to all event
		// handlers (here know as event source handler) inside this aggregate, so that
		// state of Aggregate can be updated with new information. 
		// Applying events means they are immediately applied(published) to the
		// aggregate and scheduled for publication to other event handlers.
		LOGGER.info("Dispatching productCreatedEvent to all Event Handlers inside this aggregate");
		AggregateLifecycle.apply(productCreatedEvent);

	}
	
	/**
	 * When it was used as a parameterized constructor it caused problems. The replay
	 * wouldn't happen, and hence we were not able to get current quantity for cross
	 * verification
	 * 
	 */
	@CommandHandler
	public void handle(ReserveProductCommand reserveProductCommand) {

		try {

			LOGGER.info("Inside Command Handler for ReserveProductCommand");

			// There is no need to send a separate query to check quantity
			// Whenever aggregate is loaded, its state will be restored by Axion framework
			// automatically
			// Axion framework will create a new object of Aggregate class and replay the
			// events from the event store
			// to bring this aggregate to current state.
			if (quantity < reserveProductCommand.getQuantity()) {
				LOGGER.error("There isn't enough product in stock of ProductId: {}",
						reserveProductCommand.getProductId());
				throw new IllegalArgumentException(
						"There isn't enough product in stock of ProductId:" + reserveProductCommand.getProductId());
			}

			ProductReservedEvent productReservedEvent = ProductReservedEvent.builder()
					.orderId(reserveProductCommand.getOrderId()).productId(reserveProductCommand.getProductId())
					.quantity(reserveProductCommand.getQuantity()).userId(reserveProductCommand.getUserId()).build();

			LOGGER.info("Dispatching productReservedEvent to all Event Handlers inside this aggregate");
			AggregateLifecycle.apply(productReservedEvent);

		} catch (Exception e) {
			LOGGER.error("Error Occured While Reserving Product. {}", e.getMessage());
			e.printStackTrace();
			throw e;
		}

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
	 * productCreatedEvent after this method will be scheduled for publication to
	 * other event handler and then persisted in event store <br>
	 */
	@EventSourcingHandler
	public void on(ProductCreatedEvent productCreatedEvent) {
		LOGGER.info("Inside Event Source Handler for ProductCreatedEvent ");
		this.productId = productCreatedEvent.getProductId();
		this.price = productCreatedEvent.getPrice();
		this.title = productCreatedEvent.getTitle();
		this.quantity = productCreatedEvent.getQuantity();
	}
	
	@EventSourcingHandler
	public void on(ProductReservedEvent productReservedEvent) {
		LOGGER.info("Inside Event Source Handler for ProductReservedEvent to subtract the quantity ");
		//We only need to change the available  quantity. Rest w 
		this.quantity -= productReservedEvent.getQuantity();
	}
}
