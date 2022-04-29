package com.cheapbuy.ordersservice.command.rest;

import java.util.UUID;

import javax.validation.Valid;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cheapbuy.ordersservice.command.CreateOrderCommand;
import com.cheapbuy.ordersservice.core.data.OrderRestModel;
import com.cheapbuy.ordersservice.core.data.OrderStatus;

@RestController
@RequestMapping("/orders")
public class OrdersCommandController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrdersCommandController.class);
	
	private final CommandGateway commandGateway;
	
	public OrdersCommandController(CommandGateway commandGateway) {
		this.commandGateway = commandGateway;
	}
	
	@PostMapping
	public String createOrder(@Valid @RequestBody OrderRestModel orderRestModel) {
		
		String userId = "27b95829-4f3f-4ddf-8983-151ba010e35b";
		
		LOGGER.info("Rest Call received to Create new Order");
		CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
				.productId(orderRestModel.getProductId())
				.quantity(orderRestModel.getQuantity())
				.addressId(orderRestModel.getAddressId())
				.orderId(UUID.randomUUID().toString())
				.userId(userId)
				.orderStatus(OrderStatus.CREATED).build();
		String returnedValue = commandGateway.sendAndWait(createOrderCommand);
		return "Order Created " + returnedValue;
		
	}
}
