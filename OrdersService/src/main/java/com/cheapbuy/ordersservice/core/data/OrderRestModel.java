package com.cheapbuy.ordersservice.core.data;

import com.cheapbuy.ordersservice.command.CreateOrderCommand;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class OrderRestModel {

	public final String orderId;
	private final String userId;
	private final String productId;
	private final int quantity;
	private final String addressId;
	private final OrderStatus orderStatus;
}
