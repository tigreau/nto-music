package com.musicshop.pricing;

import java.math.BigDecimal;

public class CouponDiscountDecorator implements PriceCalculator {

    private final PriceCalculator delegate;
    private final BigDecimal couponAmount;

    public CouponDiscountDecorator(PriceCalculator delegate, BigDecimal couponAmount) {
        this.delegate = delegate;
        this.couponAmount = couponAmount;
    }

    @Override
    public BigDecimal calculate() {
        BigDecimal result = delegate.calculate().subtract(couponAmount);
        return result.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : result;
    }
}
