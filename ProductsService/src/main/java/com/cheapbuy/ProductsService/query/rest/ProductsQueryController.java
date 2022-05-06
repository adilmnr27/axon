package com.cheapbuy.ProductsService.query.rest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cheapbuy.ProductsService.core.data.ProductRestModel;
import com.cheapbuy.ProductsService.query.FindProductsQuery;
import com.cheapbuy.ProductsService.query.ProductQueryHandler;

@RestController
@RequestMapping("/products")
public class ProductsQueryController {


	private static final Logger LOGGER = LoggerFactory.getLogger(ProductsQueryController.class);
	
	private final QueryGateway queryGateway;
	
	public ProductsQueryController(QueryGateway queryGateway) {
		this.queryGateway = queryGateway;
	}
	
	/**
	 * http://localhost:8082/products-service/products
	 */
	@GetMapping()
	public List<ProductRestModel> getProducts(){
		
		FindProductsQuery findProductsQuery = new FindProductsQuery();
		CompletableFuture<List<ProductRestModel>> future = queryGateway.query(findProductsQuery, ResponseTypes.multipleInstancesOf(ProductRestModel.class));
		return future.join();	
		
	}
	

	
	
}
