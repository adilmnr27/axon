package com.cheapbuy.ProductsService.core.events;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

/**
 * Naming convention <Noun><ActionPerformed>Event
 * This class is upto us to decide the structure based on requirement. 
 * We need to use this event while persisting data in database. So we require the product details
 */

@Data
@Builder
public class ProductCreatedEvent {

	private final String productId;
	private final String title;
	private final BigDecimal price;
	private final Integer quantity;
}
