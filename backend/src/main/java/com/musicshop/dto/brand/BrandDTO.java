package com.musicshop.dto.brand;

public record BrandDTO(Long id, String name, String slug, String logoUrl) {

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getLogoUrl() {
        return logoUrl;
    }
}
