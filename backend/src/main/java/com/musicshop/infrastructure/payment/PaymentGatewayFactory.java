package com.musicshop.infrastructure.payment;

import com.musicshop.service.payment.PaymentGateway;
import com.musicshop.service.payment.PaymentGatewayProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentGatewayFactory implements PaymentGatewayProvider {

    private final List<PaymentGateway> gateways;

    public PaymentGatewayFactory(List<PaymentGateway> gateways) {
        this.gateways = gateways;
    }

    public PaymentGateway getGateway(String paymentMethod) {
        for (PaymentGateway gateway : gateways) {
            if (gateway.supports(paymentMethod)) {
                return gateway;
            }
        }
        throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
    }
}
