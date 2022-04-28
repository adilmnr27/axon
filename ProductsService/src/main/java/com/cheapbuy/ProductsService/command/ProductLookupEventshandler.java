package com.cheapbuy.ProductsService.command;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cheapbuy.ProductsService.core.data.ProductLookupEntity;
import com.cheapbuy.ProductsService.core.data.ProductLookupRepository;
import com.cheapbuy.ProductsService.core.events.ProductCreatedEvent;
import com.cheapbuy.ProductsService.query.ProductsEventHandler;

@Component
//Grouping multiple event handlers to same logical group. 
//By default it is package name will be used.
//So if it handlers exist in different packages, then use this annotation for logical grouping
@ProcessingGroup("product-group")
public class ProductLookupEventshandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductLookupEventshandler.class);
	
	private final ProductLookupRepository productLookupRepository;
	
	public ProductLookupEventshandler(ProductLookupRepository repository) {
		this.productLookupRepository = repository;
	}
	
	/**
	 * Consumes ProductCreatedEvent. <br>
	 * Saves the id and title of Product in productLookupTable.(which will be useful in future for set validation)<br>
	 * @param productCreatedEvent Event which it consumes <br>
	 */
	@EventHandler
	public void on(ProductCreatedEvent productCreatedEvent) {
		LOGGER.info("Inside EventHandler for Product Created Event. Proceeding to save the details in ProductLookUpTable");
		ProductLookupEntity entity = new ProductLookupEntity(productCreatedEvent.getProductId(), productCreatedEvent.getTitle());
		productLookupRepository.save(entity);
		
	}
}
