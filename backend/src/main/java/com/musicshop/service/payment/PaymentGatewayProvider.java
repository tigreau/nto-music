package com.musicshop.service.payment;

public interface PaymentGatewayProvider {

    PaymentGateway getGateway(String paymentMethod);
}
