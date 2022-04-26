package com.cheapbuy.ProductsService.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("products")
public class ProductsController {

	@Autowired
	private Environment env;
	
	@GetMapping
	public String getProduct() {
		
		return "Product Fetched " + env.getProperty("local.server.port");
		//server.port would have given 0 as that it what we have configured in appln.properties.
	}
	
	
	@PostMapping
	public String createProduct() {
		return "Product Created";
	}
	
	
	@PutMapping
	public String updateProduct() {
		return "Product updated";
	}
	
	@DeleteMapping
	public String deleteProduct() {
		return "Product Deleted";
	}
	
	
	
}
