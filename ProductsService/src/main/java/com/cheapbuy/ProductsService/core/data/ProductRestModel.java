package com.cheapbuy.ProductsService.core.data;


import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ProductRestModel {

	private String productId;
	@NotBlank(message="Product Title Cannot be Blank")
	private String title;
	@Min(value=1, message="Price cannot be zero or negative")
	private BigDecimal price;
	
	@Min(value=1, message="Quantity cannot be zero or negative")
	@Max(value=5, message="Quantity cannot be larger then 5" )
	private Integer quantity;
	
}
