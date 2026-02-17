package com.musicshop.pricing;

import java.math.BigDecimal;

public class ShippingFeeDecorator implements PriceCalculator {

    private final PriceCalculator delegate;
    private final BigDecimal shippingFee;

    public ShippingFeeDecorator(PriceCalculator delegate, BigDecimal shippingFee) {
        this.delegate = delegate;
        this.shippingFee = shippingFee;
    }

    @Override
    public BigDecimal calculate() {
        return delegate.calculate().add(shippingFee);
    }
}
