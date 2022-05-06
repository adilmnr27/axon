package com.cheapbuy.ordersservice.core.data;

import lombok.Value;

@Value
public class OrderSummary {

	private final String orderId;
	private final OrderStatus orderStatus;
}
