package com.cheapbuy.ProductsService.core.errorhandling;

import java.time.LocalDateTime;

import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.queryhandling.QueryExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ProductsServiceErrorHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductsServiceErrorHandler.class);

	@ExceptionHandler(value = { IllegalStateException.class })
	public ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
		LOGGER.error("Controller Advice : Illegal State Exception Occured {} ", ex.getMessage());
		return new ResponseEntity<>(new ErrorMessage(LocalDateTime.now(), ex.getMessage()), new HttpHeaders(),
				HttpStatus.INTERNAL_SERVER_ERROR);

	}
	
	/**
	 * Exception thrown by Command Handler methods(Aggregate class) is wrapped up by Axon in CommandExecutionException
	 */
	@ExceptionHandler(value = { CommandExecutionException.class })
	public ResponseEntity<Object> handleCommandExecutionException(CommandExecutionException ex, WebRequest request) {
		LOGGER.error("Controller Advice : Command Execution Exception Occured : {} ", ex.getLocalizedMessage() ,ex);
		return new ResponseEntity<>(new ErrorMessage(LocalDateTime.now(), ex.getMessage()), new HttpHeaders(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * Exception thrown by Query Handler is wrapped up by Axon in QueryExecutionException
	 */
	@ExceptionHandler(value = { QueryExecutionException.class })
	public ResponseEntity<Object> handleQueryExecutionException(QueryExecutionException ex, WebRequest request) {
		LOGGER.error("Controller Advice : Query Execution Exception Occured : {} ", ex.getLocalizedMessage() ,ex);
		return new ResponseEntity<>(new ErrorMessage(LocalDateTime.now(), ex.getMessage()), new HttpHeaders(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = { Exception.class })
	public ResponseEntity<Object> handleOtherException(Exception ex, WebRequest request) {
		LOGGER.error("Controller Advice : Exception Occured : {} ", ex.getLocalizedMessage());
		return new ResponseEntity<>(new ErrorMessage(LocalDateTime.now(), ex.getMessage()), new HttpHeaders(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
