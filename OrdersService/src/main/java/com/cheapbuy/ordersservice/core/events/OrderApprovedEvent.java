package com.cheapbuy.ordersservice.core.events;

import com.cheapbuy.core.events.PaymentProcessedEvent;
import com.cheapbuy.ordersservice.core.data.OrderStatus;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Value
public class OrderApprovedEvent {
	
	private final String orderId;
	private final OrderStatus orderStatus = OrderStatus.APPROVED;

}
