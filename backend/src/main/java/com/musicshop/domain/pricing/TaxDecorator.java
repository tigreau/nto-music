package com.musicshop.domain.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TaxDecorator implements PriceCalculator {

    private final PriceCalculator delegate;
    private final BigDecimal taxRate;

    public TaxDecorator(PriceCalculator delegate, BigDecimal taxRate) {
        this.delegate = delegate;
        this.taxRate = taxRate;
    }

    @Override
    public BigDecimal calculate() {
        BigDecimal subtotal = delegate.calculate();
        BigDecimal tax = subtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        return subtotal.add(tax);
    }
}
