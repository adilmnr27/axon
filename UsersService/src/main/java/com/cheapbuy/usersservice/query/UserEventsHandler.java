package com.cheapbuy.usersservice.query;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import com.cheapbuy.core.model.PaymentDetails;
import com.cheapbuy.core.model.User;
import com.cheapbuy.core.query.FetchUserPaymentDetailsQuery;

@Component
public class UserEventsHandler {

    @QueryHandler
    public User findUserPaymentDetails(FetchUserPaymentDetailsQuery query) {
        
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
