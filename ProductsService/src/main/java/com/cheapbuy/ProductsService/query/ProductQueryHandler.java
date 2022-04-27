package com.cheapbuy.ProductsService.query;

import java.util.ArrayList;
import java.util.List;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.cheapbuy.ProductsService.core.data.ProductEntity;
import com.cheapbuy.ProductsService.core.data.ProductRestModel;
import com.cheapbuy.ProductsService.core.data.ProductsRepository;

@Component
public class ProductQueryHandler {

	private final ProductsRepository productsRepository;
	
	public ProductQueryHandler(ProductsRepository productsRepository) {
		this.productsRepository= productsRepository;
	}
	
	/**
	 * The annotated method's first parameter is the query handled by that method.<br>
	 * Optionally, the query handler may specify a second parameter of type
	 * org.axonframework.messaging.MetaData. The active MetaData will bepassed if
	 * that parameter is supplied.
	 * 
	 * @return list of products from REST DB
	 */
	@QueryHandler
	public List<ProductRestModel> findProducts(FindProductsQuery findProductsQuery){
		List<ProductRestModel> productsRest = new ArrayList<>();
	
		List<ProductEntity> entities = productsRepository.findAll();
		
		for (ProductEntity productEntity : entities) {
			ProductRestModel restModel = new ProductRestModel();
			BeanUtils.copyProperties(productEntity, restModel);
			productsRest.add(restModel);
		}
		return productsRest;
		
	}
}
