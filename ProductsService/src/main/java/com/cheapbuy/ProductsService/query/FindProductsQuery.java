package com.cheapbuy.ProductsService.query;

/**
 * Naming Convention <FetchingVerb><Noun>Query <br>
 * This classes carry information needed to perform query like osrting, pagination etc.<br>
 * Here we don't need any extra information. We just want everything without any criteria
 * Hence this class is empty<br>
 * 
 * 
 * New object needs to be created everytime.
 * We have to code so that object is taken by Query Gateway and sent to Query Bus 
 * which will in turn route to ProductQueryHandler <br>
 * 
 * Class Mirror for CreateProductCommand<br>
 * 
 * 
 * Kinda reminds of Action object in React which may or may not contain the payload<br>
 *
 */
public class FindProductsQuery {

}
