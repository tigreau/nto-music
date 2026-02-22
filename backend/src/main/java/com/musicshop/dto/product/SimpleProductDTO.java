package com.musicshop.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musicshop.model.product.ProductCondition;
import java.math.BigDecimal;
import java.util.List;

public record SimpleProductDTO(
        Long id,
        String name,
        String slug,
        BigDecimal price,
        String categoryName,
        String brandName,
        ProductCondition condition,
        String thumbnailUrl,
        boolean isPromoted,
        List<ProductImageDTO> images) {

    public SimpleProductDTO {
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

    public BigDecimal getPrice() {
        return price;
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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    @JsonProperty("isPromoted")
    public boolean isPromoted() {
        return isPromoted;
    }

    public List<ProductImageDTO> getImages() {
        return images;
    }
}
