package com.cheapbuy.ordersservice.command.rest;

import java.time.Duration;
import java.util.UUID;

import javax.validation.Valid;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cheapbuy.ordersservice.command.CreateOrderCommand;
import com.cheapbuy.ordersservice.core.data.OrderRestModel;
import com.cheapbuy.ordersservice.core.data.OrderStatus;
import com.cheapbuy.ordersservice.core.data.OrderSummary;
import com.cheapbuy.ordersservice.query.FindOrderQuery;

@RestController
@RequestMapping("/orders")
public class OrdersCommandController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrdersCommandController.class);
	
	private final CommandGateway commandGateway;
	private final QueryGateway queryGateway;
	
	@Autowired
	public OrdersCommandController(CommandGateway commandGateway,QueryGateway queryGateway) {
		this.commandGateway = commandGateway;
		this.queryGateway = queryGateway;
	}
	
	@PostMapping
	public OrderSummary createOrder(@Valid @RequestBody OrderRestModel orderRestModel) {
		
		String userId = "27b95829-4f3f-4ddf-8983-151ba010e35b";
		
		LOGGER.info("Rest Call received to Create new Order");
		
		String orderId = UUID.randomUUID().toString();
		CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
				.productId(orderRestModel.getProductId())
				.quantity(orderRestModel.getQuantity())
				.addressId(orderRestModel.getAddressId())
				.orderId(orderId)
				.userId(userId)
				.orderStatus(OrderStatus.CREATED).build();
		
		SubscriptionQueryResult<OrderSummary, OrderSummary> queryResult =  queryGateway.subscriptionQuery(new FindOrderQuery(orderId), ResponseTypes.instanceOf(OrderSummary.class),
				ResponseTypes.instanceOf(OrderSummary.class));
		try {
			commandGateway.sendAndWait(createOrderCommand);
			return queryResult.updates().next().block();
		} finally {
			queryResult.close();
		}
		


	}
}
