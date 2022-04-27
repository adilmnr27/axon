package com.cheapbuy.ProductsService.command;

import java.lang.annotation.Target;
import java.math.BigDecimal;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Builder;
import lombok.Data;

/**
 * Naming convention is <verb><noun>Command
 *
 */
@Builder
@Data //will not generate setter methods as fields have been marked final
public class CreateProductCommand {

	/**
	 * Field or method level annotation that marks a field or
	 * method providing the identifier of the aggregate that a command targets. 
	 */
	@TargetAggregateIdentifier
	private final String productId;
	
	private final String title;
	private final BigDecimal price;
	private final Integer quantity;
	
}
