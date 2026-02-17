package com.musicshop.payment;

import org.springframework.stereotype.Component;

@Component
public class PaymentGatewayFactory {

    public PaymentGateway getGateway(String paymentMethod) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method must not be null");
        }

        switch (paymentMethod.toLowerCase()) {
            case "stripe":
            case "credit_card":
                return new StripePaymentAdapter();
            case "paypal":
                return new PayPalPaymentAdapter();
            default:
                throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
        }
    }
}
