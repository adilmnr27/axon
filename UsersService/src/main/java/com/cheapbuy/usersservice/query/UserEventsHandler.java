package com.cheapbuy.usersservice.query;

import org.axonframework.queryhandling.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cheapbuy.core.model.PaymentDetails;
import com.cheapbuy.core.model.User;
import com.cheapbuy.core.query.FetchUserPaymentDetailsQuery;

@Component
public class UserEventsHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserEventsHandler.class);
	
    @QueryHandler
    public User findUserPaymentDetails(FetchUserPaymentDetailsQuery query) {
        
    	LOGGER.info("Inside Query Handler for FetchUserPaymentDetailsQuery event");
        PaymentDetails paymentDetails = PaymentDetails.builder()
                .cardNumber("123Card")
                .cvv("123")
                .name("John Doe")
                .validUntilMonth(12)
                .validUntilYear(2030)
                .build();

        return User.builder()
                .firstName("John")
                .lastName("Doe")
                .userId(query.getUserId())
                .paymentDetails(paymentDetails)
                .build();

       
    }
    
    
}
