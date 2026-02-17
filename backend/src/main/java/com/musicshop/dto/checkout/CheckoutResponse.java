package com.musicshop.dto.checkout;

import java.math.BigDecimal;

public class CheckoutResponse {

    private Long orderId;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String transactionId;

    public CheckoutResponse() {
    }

    public CheckoutResponse(Long orderId, BigDecimal totalAmount, String paymentStatus, String transactionId) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
