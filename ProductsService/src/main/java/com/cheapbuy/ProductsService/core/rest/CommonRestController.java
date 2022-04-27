package com.cheapbuy.ProductsService.core.rest;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CommonRestController {

	private final Environment env;

	public CommonRestController(Environment env) {
		this.env = env;
	}
	
	@GetMapping("/ping")
	public String getProduct() {
		return "Pinged " + env.getProperty("spring.application.name") + " running on " +env.getProperty("local.server.port");
		//server.port would have given 0 as that it what we have configured in appln.properties.
	}
	
}
