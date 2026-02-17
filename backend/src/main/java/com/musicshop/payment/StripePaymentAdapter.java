package com.musicshop.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class StripePaymentAdapter implements PaymentGateway {

    private static final Logger logger = LoggerFactory.getLogger(StripePaymentAdapter.class);

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        logger.info("Processing Stripe payment of {} {}", request.getAmount(), request.getCurrency());

        // Simulated Stripe API call
        String transactionId = "stripe_" + UUID.randomUUID().toString().substring(0, 8);
        logger.info("Stripe transaction completed: {}", transactionId);

        return PaymentResult.successful(transactionId);
    }
}
