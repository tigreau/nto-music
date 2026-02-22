package com.musicshop.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musicshop.model.product.ProductCondition;
import java.math.BigDecimal;
import java.util.List;

public record DetailedProductDTO(
        Long id,
        String name,
        String slug,
        String description,
        BigDecimal price,
        Integer quantityAvailable,
        String categoryName,
        String brandName,
        ProductCondition condition,
        String conditionNotes,
        boolean isPromoted,
        List<ProductImageDTO> images) {

    public DetailedProductDTO {
        images = (images == null) ? List.of() : List.copyOf(images);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantityAvailable() {
        return quantityAvailable;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getBrandName() {
        return brandName;
    }

    public ProductCondition getCondition() {
        return condition;
    }

    public String getConditionNotes() {
        return conditionNotes;
    }

    @JsonProperty("isPromoted")
    public boolean isPromoted() {
        return isPromoted;
    }

    public List<ProductImageDTO> getImages() {
        return images;
    }
}
