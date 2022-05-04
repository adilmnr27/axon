package com.cheapbuy.paymentsservice.query;


import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cheapbuy.core.events.PaymentProcessedEvent;
import com.cheapbuy.paymentsservice.core.data.PaymentEntity;
import com.cheapbuy.paymentsservice.core.data.PaymentRepository;

@Component
public class PaymentEventsHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(PaymentEventsHandler.class);
    private final PaymentRepository paymentRepository;

    public PaymentEventsHandler(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @EventHandler
    public void on(PaymentProcessedEvent event) {
		LOGGER.info(
				"PaymentProcessedEvent is called for orderId: {}. Proceeding to save the payment process details in payments database",
				event.getOrderId());

        PaymentEntity paymentEntity = PaymentEntity.builder().orderId(event.getOrderId())
        		.paymentId(event.getPaymentId()).build();

        paymentRepository.save(paymentEntity);
    }
}