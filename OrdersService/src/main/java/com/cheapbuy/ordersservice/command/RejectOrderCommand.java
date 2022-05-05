package com.cheapbuy.ordersservice.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Builder;
import lombok.Value;


@Value
@Builder
public class RejectOrderCommand {

	@TargetAggregateIdentifier
	private final String orderId;
	private final String cancellationReason;
}
