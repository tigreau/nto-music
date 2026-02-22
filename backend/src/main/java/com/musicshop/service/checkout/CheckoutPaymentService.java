package com.musicshop.service.checkout;

import com.musicshop.exception.PaymentFailedException;
import com.musicshop.service.payment.PaymentGateway;
import com.musicshop.service.payment.PaymentGatewayProvider;
import com.musicshop.service.payment.PaymentRequest;
import com.musicshop.service.payment.PaymentResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CheckoutPaymentService {

    private final PaymentGatewayProvider paymentGatewayProvider;

    public CheckoutPaymentService(PaymentGatewayProvider paymentGatewayProvider) {
        this.paymentGatewayProvider = paymentGatewayProvider;
    }

    public PaymentResult processPayment(String paymentMethod, BigDecimal totalAmount) {
        PaymentGateway gateway = paymentGatewayProvider.getGateway(paymentMethod);
        PaymentRequest paymentRequest = new PaymentRequest(totalAmount, "EUR", paymentMethod);
        PaymentResult paymentResult = gateway.processPayment(paymentRequest);
        if (!paymentResult.isSuccess()) {
            throw new PaymentFailedException("Payment failed: " + paymentResult.getMessage());
        }
        return paymentResult;
    }
}
