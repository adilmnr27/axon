package com.cheapbuy.ProductsService.core.query;

import org.axonframework.eventhandling.EventHandler;
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
public class ProductsEventHandler {
	
	public final ProductsRepository productsRepository;
	
	public ProductsEventHandler(ProductsRepository productsRepository) {
		this.productsRepository = productsRepository;
	}

	/**
	 * Will get invoked if ProductCreatedEvent has been dispatchd
	 * @param event Dispatched even which will invoke this method
	 */
	@EventHandler
	public void on(ProductCreatedEvent event) {
		ProductEntity entityToBeSaved = new ProductEntity();
		BeanUtils.copyProperties(event, entityToBeSaved);
		productsRepository.save(entityToBeSaved);
	}
}
