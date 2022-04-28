package com.cheapbuy.ProductsService.command.rest;

import java.util.UUID;

import javax.validation.Valid;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cheapbuy.ProductsService.command.CreateProductCommand;
import com.cheapbuy.ProductsService.core.data.ProductRestModel;



@RestController
@RequestMapping("/products")
public class ProductsCommandController {
	

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductsCommandController.class);

	/**
	 * Think of it as an API to send commands to Command Bus. (Command Bus routes commands to Command Handler)
	 */
	private final CommandGateway commandGateway;
	
	//constructor based injection
	public ProductsCommandController( CommandGateway commandGateway) {
		this.commandGateway=commandGateway;
	}
	
	
	@PostMapping
	public String createProduct(@Valid @RequestBody ProductRestModel product) {
		
			CreateProductCommand createProductCommand = CreateProductCommand.builder().price(product.getPrice())
					.title(product.getTitle()).quantity(product.getQuantity()).productId(UUID.randomUUID().toString())
					.build();

			String returnedValue = commandGateway.sendAndWait(createProductCommand);
			//Exception handled in Controller Advice
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
