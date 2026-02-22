package com.musicshop.service.payment;

public interface PaymentGateway {
    boolean supports(String paymentMethod);

    PaymentResult processPayment(PaymentRequest request);
}
