package com.musicshop.service.checkout;

import com.musicshop.model.cart.CartDetail;
import com.musicshop.domain.pricing.BasePriceCalculator;
import com.musicshop.domain.pricing.CouponDiscountDecorator;
import com.musicshop.domain.pricing.PriceCalculator;
import com.musicshop.domain.pricing.ShippingFeeDecorator;
import com.musicshop.domain.pricing.TaxDecorator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CheckoutPricingService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.21");
    private static final BigDecimal SHIPPING_FEE = new BigDecimal("5.99");

    public BigDecimal calculateTotal(List<CartDetail> cartDetails, String couponCode, BigDecimal couponAmount) {
        BigDecimal subtotal = cartDetails.stream()
                .map(cd -> cd.getProduct().getPrice().multiply(BigDecimal.valueOf(cd.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PriceCalculator calculator = new BasePriceCalculator(subtotal);
        if (couponCode != null && !couponCode.isBlank()) {
            calculator = new CouponDiscountDecorator(calculator, couponAmount);
        }
        calculator = new TaxDecorator(calculator, TAX_RATE);
        calculator = new ShippingFeeDecorator(calculator, SHIPPING_FEE);

        return calculator.calculate();
    }
}
