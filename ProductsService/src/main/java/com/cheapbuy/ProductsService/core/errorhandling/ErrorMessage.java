package com.cheapbuy.ProductsService.core.errorhandling;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ErrorMessage {

	private final LocalDateTime timeStamp;
	private final String message;
}
