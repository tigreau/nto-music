package com.musicshop.infrastructure.payment;

import com.musicshop.service.payment.PaymentGateway;
import com.musicshop.service.payment.PaymentRequest;
import com.musicshop.service.payment.PaymentResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

@Component
public class StripePaymentAdapter implements PaymentGateway {

    private static final Logger logger = LoggerFactory.getLogger(StripePaymentAdapter.class);

    @Override
    public boolean supports(String paymentMethod) {
        if (paymentMethod == null) {
            return false;
        }
        String normalized = paymentMethod.trim().toLowerCase(Locale.ROOT);
        return "stripe".equals(normalized) || "credit_card".equals(normalized);
    }

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        logger.info("Processing Stripe payment of {} {}", request.getAmount(), request.getCurrency());

        // Simulated Stripe API call
        String transactionId = "stripe_" + UUID.randomUUID().toString().substring(0, 8);
        logger.info("Stripe transaction completed: {}", transactionId);

        return PaymentResult.successful(transactionId);
    }
}
