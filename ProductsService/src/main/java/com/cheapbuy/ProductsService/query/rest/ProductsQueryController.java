package com.cheapbuy.ProductsService.query.rest;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("products")
public class ProductsQueryController {

	private final Environment env;

	public ProductsQueryController(Environment env) {
		this.env = env;
	}
	
	@GetMapping("ping")
	public String getProduct() {
		return "Pinged " + env.getProperty("local.server.port");
		//server.port would have given 0 as that it what we have configured in appln.properties.
	}
	
	
}
