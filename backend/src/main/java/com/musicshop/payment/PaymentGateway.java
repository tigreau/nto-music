package com.musicshop.payment;

public interface PaymentGateway {
    PaymentResult processPayment(PaymentRequest request);
}
