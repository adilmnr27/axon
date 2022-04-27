package com.cheapbuy.ProductsService.rest;

import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cheapbuy.ProductsService.command.CreateProductCommand;

@RestController
@RequestMapping("products")
public class ProductsController {


	private final Environment env;
	/**
	 * Think of it as an API to send commands to Command Bus. (Command Bus routes commands to Command Handler)
	 */
	private final CommandGateway commandGateway;
	
	//constructor based injection
	public ProductsController(Environment env, CommandGateway commandGateway) {
		this.env=env;
		this.commandGateway=commandGateway;
	}
	
	@GetMapping
	public String getProduct() {
		
		return "Product Fetched " + env.getProperty("local.server.port");
		//server.port would have given 0 as that it what we have configured in appln.properties.
	}
	
	@PostMapping
	public String createProduct(@RequestBody ProductPojo product) {
		CreateProductCommand createProductCommand = CreateProductCommand.builder().price(product.getPrice())
				.title(product.getTitle()).quantity(product.getQuantity()).productId(UUID.randomUUID().toString())
				.build();

		String returnedValue = commandGateway.sendAndWait(createProductCommand);
		return "Product Created " + returnedValue;
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
