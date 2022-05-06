package com.cheapbuy.ordersservice.query;

import lombok.Value;

@Value
public class FindOrderQuery {
	
	private final String orderId;
}
