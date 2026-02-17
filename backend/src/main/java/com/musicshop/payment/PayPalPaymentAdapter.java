package com.musicshop.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class PayPalPaymentAdapter implements PaymentGateway {

    private static final Logger logger = LoggerFactory.getLogger(PayPalPaymentAdapter.class);

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        logger.info("Processing PayPal payment of {} {}", request.getAmount(), request.getCurrency());

        // Simulated PayPal API call
        String transactionId = "paypal_" + UUID.randomUUID().toString().substring(0, 8);
        logger.info("PayPal transaction completed: {}", transactionId);

        return PaymentResult.successful(transactionId);
    }
}
