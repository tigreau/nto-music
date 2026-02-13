package com.musicshop.discount;

import java.util.Arrays;
import java.util.Optional;

public enum DiscountType {
    FIXED_AMOUNT("Fixed Amount"),
    PERCENTAGE("Percentage");

    private final String value;

    DiscountType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Optional<DiscountType> fromValue(String value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equalsIgnoreCase(value))
                .findFirst();
    }
}
