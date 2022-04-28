package com.cheapbuy.ProductsService.core.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lookup table invoked by command message interceptor to check if product already exists before passing it to command bus. 
 * Based on Set Based Consistency Validation CQRS design pattern
 *
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="product_lookup")
public class ProductLookupEntity {

	@Id
	@Column(unique=true)
	private String productId;
	
	private String title;
}
