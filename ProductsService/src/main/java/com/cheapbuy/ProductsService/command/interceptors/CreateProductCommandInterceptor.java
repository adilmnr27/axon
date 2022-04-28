package com.cheapbuy.ProductsService.command.interceptors;

import java.util.List;
import java.util.function.BiFunction;

import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cheapbuy.ProductsService.command.CreateProductCommand;
import com.cheapbuy.ProductsService.core.data.ProductLookupEntity;
import com.cheapbuy.ProductsService.core.data.ProductLookupRepository;

/**
 * Interceptor intercepts between CommandGateway and CommandBus. <br>
 * Various tasks such as validation logging could be performed <br>
 * Make sure that this interceptor is registered with Command Bus <br>
 *
 */
@Component
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CreateProductCommandInterceptor.class);

	private final ProductLookupRepository productLookupRepository;

	public CreateProductCommandInterceptor(ProductLookupRepository productLookupRepository) {
		this.productLookupRepository = productLookupRepository;
	}

	@Override
	public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
			List<? extends CommandMessage<?>> messages) {
		LOGGER.info("In Create Product Command Interceptor");

		return (index, command) -> {
			LOGGER.info("The command is of type: {}", command.getPayloadType());

			// This intercepter will intercept all commands.
			// Hence, Checking if Command which has been intercepted is CreateProductCommand
			if (CreateProductCommand.class.equals(command.getPayloadType())) {
				LOGGER.info("Interceptor: The Command is of type Create Product Command");

				// Casting to CreateProductCommand
				CreateProductCommand cpCommand = (CreateProductCommand) command.getPayload();

				LOGGER.info("Interceptor: Command id is {} and Command title is {} ", cpCommand.getProductId(),
						cpCommand.getTitle());

				// Perform any task we want (logging/validation/lookup etc)

				
				// Performing a lookup in lookup table to check if title or id already exists
				ProductLookupEntity productLookupEntity = productLookupRepository
						.findByProductIdOrTitle(cpCommand.getProductId(), cpCommand.getTitle());
				if (productLookupEntity != null) {
					LOGGER.error("Product with {} id and {} title already exists. Cannot proceed further",
									cpCommand.getProductId(), cpCommand.getTitle());
					throw new IllegalArgumentException(
							String.format("Product with %s id and %s title already exists",
									cpCommand.getProductId(), cpCommand.getTitle()));

				}

			}
			return command;

		};
	}

}
