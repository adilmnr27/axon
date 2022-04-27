package com.cheapbuy.ProductsService.core.data;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * Persisting in ReadDB for quering purposes
 *
 */
@Entity
@Data
@Table(name="products")
public class ProductEntity {
	@Id
	@Column(unique = true)
	private String productId;
	
	@Column(unique = true)
	private String title;
	
	private BigDecimal price;
	private Integer quantity;
}
