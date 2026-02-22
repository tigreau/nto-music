package com.musicshop.dto.cart;

import java.math.BigDecimal;

public record CartItemDTO(
        Long id,
        CartProductDTO product,
        Integer quantity,
        BigDecimal subTotal) {

    public Long getId() {
        return id;
    }

    public CartProductDTO getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public CartItemDTO withSubTotal(BigDecimal newSubTotal) {
        return new CartItemDTO(id, product, quantity, newSubTotal);
    }
}
