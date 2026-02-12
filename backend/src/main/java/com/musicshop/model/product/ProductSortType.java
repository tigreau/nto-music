package com.musicshop.model.product;

import java.util.Arrays;

public enum ProductSortType {
    PRICE_ASC("price_asc"),
    PRICE_DESC("price_desc"),
    NEWEST("newest"),
    RECOMMENDED("recommended");

    private final String value;

    ProductSortType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static java.util.Optional<ProductSortType> fromValue(String value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equalsIgnoreCase(value))
                .findFirst();
    }
}
