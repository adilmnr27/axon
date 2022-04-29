package com.cheapbuy.ordersservice.core.events;

import com.cheapbuy.ordersservice.core.data.OrderStatus;
import lombok.Builder;
import lombok.Data;



/**
 * Naming convention <Noun><ActionPerformed>Event
 * This class is upto us to decide the structure based on requirement. 
 * We need to use this event while persisting data in database. So we require the product details
 */

@Data
@Builder
public class OrderCreatedEvent {
	public final String orderId;
	private final String userId;
	private final String productId;
	private final int quantity;
	private final String addressId;
	private final OrderStatus orderStatus;
}
