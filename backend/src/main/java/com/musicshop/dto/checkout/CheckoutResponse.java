package com.musicshop.dto.checkout;

import java.math.BigDecimal;

public record CheckoutResponse(
        Long orderId,
        BigDecimal totalAmount,
        String paymentStatus,
        String transactionId) {

    public Long getOrderId() {
        return orderId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
