package com.cheapbuy.ordersservice.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.cheapbuy.ordersservice.core.data.OrderStatus;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateOrderCommand {

	/**
	 * Field or method level annotation that marks a field or
	 * method providing the identifier of the aggregate that a command targets. 
	 */
	@TargetAggregateIdentifier
	private final String orderId;
	
	private final String userId;
	private final String productId;
	private final Integer quantity;
	private final String addressId;
	private final OrderStatus orderStatus;
}
