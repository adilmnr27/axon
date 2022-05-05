package com.cheapbuy.ordersservice.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.cheapbuy.ordersservice.command.OrderAggregate;
import com.cheapbuy.ordersservice.core.data.OrderEntity;
import com.cheapbuy.ordersservice.core.data.OrderStatus;
import com.cheapbuy.ordersservice.core.data.OrdersRepository;
import com.cheapbuy.ordersservice.core.events.OrderApprovedEvent;
import com.cheapbuy.ordersservice.core.events.OrderCreatedEvent;
import com.cheapbuy.ordersservice.core.events.OrderRejectedEvent;

/**
 * Also know as projection class
 *
 */
@Component
//Grouping multiple event handlers to same logical group. 
//By default it is package name will be used.
//So if it handlers exist in different packages, then use this annotation for logical grouping
@ProcessingGroup("order-group")
public class OrdersEventsHandler {

	
	private static final Logger LOGGER = LoggerFactory.getLogger(OrdersEventsHandler.class);
	
    private final OrdersRepository ordersRepository;
    
    public OrdersEventsHandler(OrdersRepository ordersRepository) {
    	this.ordersRepository=ordersRepository;
    }
    
	/**
	 * Consumes OrderCreatedEvent. Saves the ProductEntity in Query (Read)
	 * Database
	 * 
	 * @param event Dispatched event which will be consumed
	 */
    @EventHandler
    public void on(OrderCreatedEvent event) {
    	
    	LOGGER.info("Inside EventHandler for OrderCreatedEvent. Proceding to save product in Query Database");
        OrderEntity orderEntity = new OrderEntity();
        BeanUtils.copyProperties(event, orderEntity);
 
        this.ordersRepository.save(orderEntity);
    }
    
	/**
	 * Consumes OrderApprovedEvent. Updates the order status in database
	 * 
	 * @param event Dispatched event which will be consumed
	 */
    @EventHandler
    public void on(OrderApprovedEvent event) {
    	LOGGER.info("Inside EventHandler for OrderApprovedEvent.  Proceding to Update the order status in Query database to Approved");
        OrderEntity orderEntity = this.ordersRepository.findByOrderId(event.getOrderId());
        if(orderEntity==null) {
        	LOGGER.error("Order Entity Not Found for order id {}", event.getOrderId());
        	//TODO do something about it
        }
        orderEntity.setOrderStatus(OrderStatus.APPROVED);
        this.ordersRepository.save(orderEntity);
    }
    
    /**
	 * Consumes {@code OrderRejectedEvent} . Updates the order status in database
	 * 
	 * @param event Dispatched event which will be consumed
	 */
    @EventHandler
    public void on(OrderRejectedEvent event) {
    	LOGGER.info("Inside EventHandler for OrderRejectedEvent.  Proceding to Update the order status in Query database to Rejected");
        OrderEntity orderEntity = this.ordersRepository.findByOrderId(event.getOrderId());
        if(orderEntity==null) {
        	LOGGER.error("Order Entity Not Found for order id {}", event.getOrderId());
        	//TODO do something about it
        }
        orderEntity.setOrderStatus(OrderStatus.REJECTED);
        this.ordersRepository.save(orderEntity);
    }
    
}
