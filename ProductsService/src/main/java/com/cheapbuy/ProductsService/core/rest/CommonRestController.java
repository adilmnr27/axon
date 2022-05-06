package com.cheapbuy.ProductsService.core.rest;

import java.util.Optional;

import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CommonRestController {

	private final Environment env;
	private final EventProcessingConfiguration eventProcessingConfiguration;

	public CommonRestController(Environment env, EventProcessingConfiguration eventProcessingConfiguration) {
		this.env = env;
		this.eventProcessingConfiguration = eventProcessingConfiguration;
	}

	@GetMapping("/ping")
	public String getProduct() {
		return "Pinged " + env.getProperty("spring.application.name") + " running on " +env.getProperty("local.server.port");
		//server.port would have given 0 as that it what we have configured in appln.properties.
	}
	
	
	/**
	 * http://localhost:8082/products-service/replay/product-group/reset
	 * @param processorName Processor to be reset. For example product-group
	 * @return Statements Indicating if Procesor has been reset
	 */
	@GetMapping("/replay/{processorName}/reset")
	public String replayEvents(@PathVariable String processorName) {
		Optional<TrackingEventProcessor> trackingEventProcessor = this.eventProcessingConfiguration
				.eventProcessor(processorName, TrackingEventProcessor.class);
		
		if(trackingEventProcessor.isPresent()) {
			TrackingEventProcessor eventProcessor = trackingEventProcessor.get();
			eventProcessor.shutDown();
			eventProcessor.resetTokens();
			eventProcessor.start();
			return "Processor has been reset";
		}else {
			return "No processor found. Please ensure that the name is correct";
		}
		

	}
	
	
}
