package com.cheapbuy.ProductsService;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.cheapbuy.ProductsService.command.interceptors.CreateProductCommandInterceptor;
import com.cheapbuy.ProductsService.core.errorhandling.ProductsServiceEventsErrorHandler;

@SpringBootApplication
@EnableDiscoveryClient
public class ProductsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductsServiceApplication.class, args);
	}
	
   
	/**
	 * Registering the Interceptor between Command Gateway and Command Bus
	 * 
	 * @param context Spring Container
	 * @param bus     The Command Bus
	 */
	@Autowired
	public void registerCreateProductCommandInterceptor(ApplicationContext context, CommandBus bus) {
		bus.registerDispatchInterceptor(context.getBean(CreateProductCommandInterceptor.class));
	}

	@Autowired
	public void configure(EventProcessingConfigurer config) {
		// first parameter is the name of the processing group
		// second paramter is the function that builds ListnerInvocationErrorHandler
		config.registerListenerInvocationErrorHandler("product-group", conf -> new ProductsServiceEventsErrorHandler());
	}
	

	/**
	 * Does the same thing as above configure method
	 * If we do not want  to create Custom Class like
	 * {@code ProductsServiceEventsErrorHandler} then use the
	 * {@code PropagatingErrorHandler} provided by axon framework
	 */
	/*
	@Autowired
	public void configure(EventProcessingConfigurer config) {
		// first parameter is the name of the processing group
		// second paramter is the function that builds ListnerInvocationErrorHandler
		config.registerListenerInvocationErrorHandler("product-group", conf -> PropagatingErrorHandler.instance());
	}
	*/
	
	/**
	 The name needs to be mentioned in appropriate Aggregate Class
	 */
	@Bean(name="productSnapshotTriggerDefinition")
	SnapshotTriggerDefinition productSnapshotTriggerDefinition(Snapshotter snapshotter) {
		return new EventCountSnapshotTriggerDefinition(snapshotter, 3);//seconn argument is number of events after which snapshot is done
	}
	
}
