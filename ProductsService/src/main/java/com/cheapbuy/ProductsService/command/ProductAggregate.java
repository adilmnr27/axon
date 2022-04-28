package com.cheapbuy.ProductsService.command;

import java.math.BigDecimal;
import java.util.UUID;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cheapbuy.ProductsService.command.interceptors.CreateProductCommandInterceptor;
import com.cheapbuy.ProductsService.core.events.ProductCreatedEvent;

import io.micrometer.core.instrument.util.StringUtils;

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

		if (StringUtils.isEmpty(createProductCommand.getTitle())) {
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
}
