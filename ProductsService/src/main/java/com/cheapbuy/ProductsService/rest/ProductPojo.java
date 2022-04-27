package com.cheapbuy.ProductsService.rest;


import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductPojo {

	private String title;
	private BigDecimal price;
	private Integer quantity;
	
}
