package com.cheapbuy.core.events;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductReservationCancelledEvent {

	private final String productId;
	private final int quantity;
	private final String orderId;
	private final String userId;
	private final String cancellationReason;
}
