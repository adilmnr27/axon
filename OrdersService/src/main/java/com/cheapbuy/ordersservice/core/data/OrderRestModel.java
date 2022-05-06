package com.cheapbuy.ordersservice.core.data;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class OrderRestModel {

	private final String orderId;
	private final String userId;
	private final String productId;
	private final Integer quantity;
	private final String addressId;
	private final OrderStatus orderStatus;
}
