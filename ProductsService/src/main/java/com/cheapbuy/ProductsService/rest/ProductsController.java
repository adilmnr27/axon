package com.cheapbuy.ProductsService.rest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("products")
public class ProductsController {

	
	@GetMapping
	public String getProduct() {
		return "Product Fetched";
	};
	
	
	@PostMapping
	public String createProduct() {
		return "Product Created";
	};
	
	
	@PutMapping
	public String updateProduct() {
		return "Product updated";
	};
	
	@DeleteMapping
	public String deleteProduct() {
		return "Product Deleted";
	}
	
	
	
}
