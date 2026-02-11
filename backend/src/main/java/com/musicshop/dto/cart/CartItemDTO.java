package com.musicshop.dto.cart;

import java.math.BigDecimal;

public class CartItemDTO {
    private Long id;
    private CartProductDTO product;
    private int quantity;
    private BigDecimal subTotal;

    public CartItemDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CartProductDTO getProduct() {
        return product;
    }

    public void setProduct(CartProductDTO product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }
}
