package com.musicshop.dto.cart;

import com.musicshop.dto.brand.BrandDTO;
import com.musicshop.dto.category.CategoryDTO;
import java.math.BigDecimal;

public record CartProductDTO(
        Long id,
        String name,
        String slug,
        BigDecimal price,
        String thumbnailUrl,
        CategoryDTO category,
        BrandDTO brand) {

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public BrandDTO getBrand() {
        return brand;
    }
}
