package com.musicshop.domain.discount;

import com.musicshop.model.product.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FixedAmountDiscountStrategy implements DiscountStrategy {
    private final BigDecimal discountAmount;

    public FixedAmountDiscountStrategy(@Value("${discount.fixedAmount}") BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    @Override
    public BigDecimal applyDiscount(Product product) {
        return product.getPrice().subtract(discountAmount).max(BigDecimal.ZERO);
    }
}