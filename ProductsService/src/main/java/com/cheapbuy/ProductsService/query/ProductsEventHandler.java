package com.cheapbuy.ProductsService.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.cheapbuy.ProductsService.core.data.ProductEntity;
import com.cheapbuy.ProductsService.core.data.ProductsRepository;
import com.cheapbuy.ProductsService.core.events.ProductCreatedEvent;

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
	 * Consumes ProductCreatedEvent.
	 * Saves the ProductEntity in Query (Read) Database
	 * 
	 * @param event Dispatched event which will invoke this method
	 */
	@EventHandler
	public void on(ProductCreatedEvent event) {
		LOGGER.info("Inside Event Handler for ProductCreatedEvent. Proceding to save product in Query Database");
		ProductEntity entityToBeSaved = new ProductEntity();
		BeanUtils.copyProperties(event, entityToBeSaved);
		productsRepository.save(entityToBeSaved);
	}
}
