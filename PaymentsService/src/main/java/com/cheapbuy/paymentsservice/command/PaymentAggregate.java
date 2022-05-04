package com.cheapbuy.paymentsservice.command;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cheapbuy.core.commands.ProcessPaymentCommand;
import com.cheapbuy.core.events.PaymentProcessedEvent;
import com.cheapbuy.core.model.PaymentDetails;

@Aggregate // Axon framework will know its an Aggregate class
public class PaymentAggregate {

	/**
	 * Will be used to initialize the current state of the Aggregate with the latest
	 * information.
	 * 
	 */
	@AggregateIdentifier
	private String paymentId;
	private String orderId;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentAggregate.class);
	
	/*
	 * Default constructor required by axon framework
	 */
	public PaymentAggregate() {
		
	}
	
	
	@CommandHandler
	public PaymentAggregate(ProcessPaymentCommand processPaymentCommand) {
		LOGGER.info("PaymentAggregate: Inside Command Handler for ProcessPaymentCommand");
		
		if(processPaymentCommand.getPaymentDetails() == null) {
    		throw new IllegalArgumentException("Missing payment details");
    	}
    	
    	if(processPaymentCommand.getOrderId() == null) {
    		throw new IllegalArgumentException("Missing orderId");
    	}
    	
    	if(processPaymentCommand.getPaymentId() == null) {
    		throw new IllegalArgumentException("Missing paymentId");
    	}
	
        AggregateLifecycle.apply(new PaymentProcessedEvent(processPaymentCommand.getOrderId(), 
                processPaymentCommand.getPaymentId()));
    }

    @EventSourcingHandler
    protected void on(PaymentProcessedEvent paymentProcessedEvent){
        this.paymentId = paymentProcessedEvent.getPaymentId();
        this.orderId = paymentProcessedEvent.getOrderId();
    }
	
}
