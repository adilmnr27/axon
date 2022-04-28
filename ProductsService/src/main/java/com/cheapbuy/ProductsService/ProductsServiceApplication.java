package com.cheapbuy.ProductsService;

import org.axonframework.commandhandling.CommandBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;

import com.cheapbuy.ProductsService.command.CreateProductCommand;
import com.cheapbuy.ProductsService.command.interceptors.CreateProductCommandInterceptor;

@SpringBootApplication
@EnableDiscoveryClient
public class ProductsServiceApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ProductsServiceApplication.class, args);
	}
	
	/**
	 * Registering the Interceptor between Command Gateway and Command Bus
	 * @param context Spring Container
	 * @param bus The Command Bus
	 */
	@Autowired
	public void registerCreateProductCommandInterceptor(ApplicationContext context, CommandBus bus) {
		bus.registerDispatchInterceptor(context.getBean(CreateProductCommandInterceptor.class));
	}

}
