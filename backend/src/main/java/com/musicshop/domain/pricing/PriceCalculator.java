package com.musicshop.domain.pricing;

import java.math.BigDecimal;

public interface PriceCalculator {
    BigDecimal calculate();
}
