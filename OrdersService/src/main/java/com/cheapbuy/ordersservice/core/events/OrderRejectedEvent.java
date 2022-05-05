package com.cheapbuy.ordersservice.core.events;

import com.cheapbuy.ordersservice.core.data.OrderStatus;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderRejectedEvent {

	private final String orderId;
	private final String cancellationReason;
	private final OrderStatus orderStatus = OrderStatus.REJECTED;

}
