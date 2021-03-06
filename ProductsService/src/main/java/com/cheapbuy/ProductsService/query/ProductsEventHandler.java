package com.cheapbuy.ProductsService.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ListenerInvocationErrorHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.cheapbuy.ProductsService.core.data.ProductEntity;
import com.cheapbuy.ProductsService.core.data.ProductsRepository;
import com.cheapbuy.ProductsService.core.events.ProductCreatedEvent;
import com.cheapbuy.core.events.ProductReservationCancelledEvent;
import com.cheapbuy.core.events.ProductReservedEvent;


/**
 * Also know as projection class
 *
 */
@Component
//Grouping multiple event handlers to same logical group. 
//By default it is package name will be used.
//So if it handlers exist in different packages, then use this annotation for logical grouping
@ProcessingGroup("product-group")
public class ProductsEventHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductsEventHandler.class);

	public final ProductsRepository productsRepository;

	public ProductsEventHandler(ProductsRepository productsRepository) {
		this.productsRepository = productsRepository;
	}

	/**
	 * Will handle {@code Exception} exception thrown by Event Handler methods of
	 * this class only.<br>
	 * Propogates the exception to {@code PropogationErrorHandler} or implementation
	 * of {@code ListenerInvocationErrorHandler} <br>
	 */
	@ExceptionHandler(resultType = Exception.class) // By axon framework
	public void handleOtherException(Exception ex) throws Exception {
		LOGGER.error("Inside @ExceptionHandler of Axon : Exception Occured in one of the event handler methods {}",
				ex.getMessage(), ex);
		throw ex;
	}

	/**
	 * Will handle {@code IllegalStateException} exception thrown by Event Handler
	 * methods of this class only.
	 */
	@ExceptionHandler(resultType = IllegalStateException.class) // By axon framework
	public void handleIllegalStateException(IllegalStateException ex) {
		LOGGER.error(
				"Inside @ExceptionHandler of Axon : Illegal State ExceptionOccured in one of the event handler methods {}",
				ex.getMessage(), ex);
	}

	/**
	 * Consumes ProductCreatedEvent. Saves the ProductEntity in Query (Read)
	 * Database
	 * 
	 * @param event Dispatched event which will be consumed
	 */
	@EventHandler
	public void on(ProductCreatedEvent event) {
		LOGGER.info("Inside EventHandler for ProductCreatedEvent. Proceding to save product in Query Database");
		ProductEntity entityToBeSaved = new ProductEntity();
		BeanUtils.copyProperties(event, entityToBeSaved);
		productsRepository.save(entityToBeSaved);
	}
	
	/**
	 * Consumes ProductReservedEvent. Updates the ProductEntity in Query (Read)
	 * Database
	 * 
	 * @param event Dispatched event which will be consumed
	 */
	@EventHandler
	public void on(ProductReservedEvent event) {
		LOGGER.info("Inside EventHandler for ProductReservedEvent. Proceding to update product quantity in Query Database for productid {}" , event.getProductId());
		ProductEntity entityToBeUpdated= this.productsRepository.findByProductId(event.getProductId());
		entityToBeUpdated.setQuantity(entityToBeUpdated.getQuantity()-event.getQuantity());
		productsRepository.save(entityToBeUpdated);
	}
	
	/**
	 * Consumes {@code ProductReservationCancelledEvent} Updates/adds the ProductEntity in Query (Read)
	 * Database
	 * 
	 * @param event Dispatched event which will be consumed
	 */
	@EventHandler
	public void on(ProductReservationCancelledEvent event) {
		LOGGER.info("Inside EventHandler for ProductReservationCancelledEvent. Proceding to update product quantity in Query Database for productid {}" , event.getProductId());
		ProductEntity entityToBeUpdated= this.productsRepository.findByProductId(event.getProductId());
		entityToBeUpdated.setQuantity(entityToBeUpdated.getQuantity()+event.getQuantity());
		productsRepository.save(entityToBeUpdated);
	}
	
	@ResetHandler
	public void reset() {
		LOGGER.warn("Reset Handler Called to Empty Products Table");
		productsRepository.deleteAll();
	}
	
}
