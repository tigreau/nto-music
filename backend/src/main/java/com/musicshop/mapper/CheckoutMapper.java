package com.musicshop.mapper;

import com.musicshop.dto.checkout.CheckoutResponse;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper(config = CentralMapperConfig.class)
public interface CheckoutMapper {

    default CheckoutResponse toCheckoutResponse(Long orderId, BigDecimal totalAmount, String paymentStatus, String transactionId) {
        return new CheckoutResponse(orderId, totalAmount, paymentStatus, transactionId);
    }
}
