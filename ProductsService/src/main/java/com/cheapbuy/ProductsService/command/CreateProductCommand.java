package com.cheapbuy.ProductsService.command;

import java.lang.annotation.Target;
import java.math.BigDecimal;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Builder;
import lombok.Data;

/**
 * Naming convention is <verb><noun>Command
 * 
 * This object will be accepted in ProductAggregate<br>
 * 
 * New object will be created everytime. 
 * We have to code so that object is taken by Command Gateway and 
 * sent to Command bus which will in turn route to CommandHandler(present in Aggregate Class)<br>
 * 
 * Class Mirror for FindProductQuery<br>
 * 
 * Kinda reminds me of Action object of React<br>
 * 
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
