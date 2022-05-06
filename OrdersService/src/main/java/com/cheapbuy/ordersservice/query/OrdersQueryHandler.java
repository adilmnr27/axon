package com.cheapbuy.ordersservice.query;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import com.cheapbuy.ordersservice.core.data.OrderEntity;
import com.cheapbuy.ordersservice.core.data.OrderSummary;
import com.cheapbuy.ordersservice.core.data.OrdersRepository;

/**
 * Can also be called Orders Projection Class
 *
 */
@Component
public class OrdersQueryHandler {
	
	private final OrdersRepository ordersRepository;
	
	public OrdersQueryHandler(OrdersRepository ordersRepository ) {
		this.ordersRepository = ordersRepository;
	}

	@QueryHandler
	public OrderSummary findOrder(FindOrderQuery findOrderQuery) {
		
		OrderEntity entity = ordersRepository.findByOrderId(findOrderQuery.getOrderId());
		return new OrderSummary(entity.getOrderId(), entity.getOrderStatus());
	}

}
