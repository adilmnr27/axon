package com.cheapbuy.ProductsService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ProductsServiceApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ProductsServiceApplication.class, args);
	}

}
