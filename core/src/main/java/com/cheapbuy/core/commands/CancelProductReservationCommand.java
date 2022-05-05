package com.cheapbuy.core.commands;

import java.math.BigDecimal;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CancelProductReservationCommand {

	
	/**
	 * Field or method level annotation that marks a field or
	 * method providing the identifier of the aggregate that a command targets. 
	 */
	@TargetAggregateIdentifier //will be handled by products aggregate service
	private final String productId;
	private final int quantity;
	private final String orderId;
	private final String userId;
	private final String cancellationReason;
	
}
