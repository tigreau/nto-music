package com.musicshop.discount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiscountStrategyFactory {

    private final FixedAmountDiscountStrategy fixedAmountDiscountStrategy;
    private final PercentageDiscountStrategy percentageDiscountStrategy;

    @Autowired
    public DiscountStrategyFactory(FixedAmountDiscountStrategy fixedAmountDiscountStrategy,
                                   PercentageDiscountStrategy percentageDiscountStrategy) {
        this.fixedAmountDiscountStrategy = fixedAmountDiscountStrategy;
        this.percentageDiscountStrategy = percentageDiscountStrategy;
    }

    public DiscountStrategy getDiscountStrategy(DiscountType type) {
        switch (type) {
            case FIXED_AMOUNT:
                return fixedAmountDiscountStrategy;
            case PERCENTAGE:
                return percentageDiscountStrategy;
            default:
                throw new IllegalArgumentException("Unsupported discount type: " + type);
        }
    }
}
