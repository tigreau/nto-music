package com.musicshop.infrastructure.payment;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentGatewayFactoryTest {

    private final PaymentGatewayFactory factory = new PaymentGatewayFactory(
            List.of(new StripePaymentAdapter(), new PayPalPaymentAdapter()));

    @Test
    void resolvesStripeAliases() {
        assertInstanceOf(StripePaymentAdapter.class, factory.getGateway("stripe"));
        assertInstanceOf(StripePaymentAdapter.class, factory.getGateway("credit_card"));
    }

    @Test
    void resolvesPaypalAlias() {
        assertInstanceOf(PayPalPaymentAdapter.class, factory.getGateway("paypal"));
    }

    @Test
    void rejectsUnsupportedMethod() {
        assertThrows(IllegalArgumentException.class, () -> factory.getGateway("bank_transfer"));
    }
}
