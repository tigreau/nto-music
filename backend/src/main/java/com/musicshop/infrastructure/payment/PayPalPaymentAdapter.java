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
public class PayPalPaymentAdapter implements PaymentGateway {

    private static final Logger logger = LoggerFactory.getLogger(PayPalPaymentAdapter.class);

    @Override
    public boolean supports(String paymentMethod) {
        if (paymentMethod == null) {
            return false;
        }
        String normalized = paymentMethod.trim().toLowerCase(Locale.ROOT);
        return "paypal".equals(normalized);
    }

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        logger.info("Processing PayPal payment of {} {}", request.getAmount(), request.getCurrency());

        // Simulated PayPal API call
        String transactionId = "paypal_" + UUID.randomUUID().toString().substring(0, 8);
        logger.info("PayPal transaction completed: {}", transactionId);

        return PaymentResult.successful(transactionId);
    }
}
