package com.cheapbuy.ProductsService.core.errorhandling;

import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.EventMessageHandler;
import org.axonframework.eventhandling.ListenerInvocationErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cheapbuy.ProductsService.query.ProductsEventHandler;

/**
 * Exceptions occurred in event handler methods do not propogate.
 * Hence any exception which occurs in event handler methods DO NOT cause roll
 * back of changes made in event store or in database by other event handler<br>
 * In order to avoid the above scenario and to ensure that roll back happens
 * from everywhere , this class is written. <br>
 * Will only work if the event handlers processing group is configured to use
 * subscribing event processor because then the methods use the same thread as
 * the ones used by publisher<br>
 * This class needs to be registered with {@code EventProcessingConfigurer}. This has been done in Main class<br>
 * 
 *
 */
public class ProductsServiceEventsErrorHandler implements ListenerInvocationErrorHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductsServiceEventsErrorHandler.class);

	/**
	 * The propogated exception will be wrapped in CommandExecutionException object
	 * and propogated till controller
	 * 
	 * @param exception The exception propogated from event handler class
	 */
	@Override
	public void onError(Exception exception, EventMessage<?> event, EventMessageHandler eventHandler) throws Exception {
		LOGGER.error("Exception Received in ListernerInvocationErrorhandler Implementation");
		throw exception;
	}

}
