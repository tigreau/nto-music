package com.musicshop.dto.product;

public record ProductImageDTO(
        Long id,
        String url,
        String altText,
        boolean isPrimary,
        int displayOrder) {

    public Long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getAltText() {
        return altText;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }
}
