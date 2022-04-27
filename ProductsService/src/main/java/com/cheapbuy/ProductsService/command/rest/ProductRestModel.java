package com.cheapbuy.ProductsService.command.rest;


import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductRestModel {

	private String title;
	private BigDecimal price;
	private Integer quantity;
	
}
