package com.musicshop.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musicshop.model.product.ProductCondition;
import java.math.BigDecimal;

public class SimpleProductDTO {
    private Long id;
    private String name;
    private String slug;
    private BigDecimal price;
    private String categoryName;
    private String brandName;
    private ProductCondition condition;
    private String thumbnailUrl;
    private boolean isPromoted;

    public SimpleProductDTO() {
    }

    public SimpleProductDTO(Long id, String name, String slug, BigDecimal price, String categoryName, String brandName,
            ProductCondition condition, String thumbnailUrl, boolean isPromoted) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.price = price;
        this.categoryName = categoryName;
        this.brandName = brandName;
        this.condition = condition;
        this.thumbnailUrl = thumbnailUrl;
        this.isPromoted = isPromoted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public ProductCondition getCondition() {
        return condition;
    }

    public void setCondition(ProductCondition condition) {
        this.condition = condition;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    @JsonProperty("isPromoted")
    public boolean isPromoted() {
        return isPromoted;
    }

    public void setPromoted(boolean promoted) {
        isPromoted = promoted;
    }

    // Images
    private java.util.List<ProductImageDTO> images;

    public java.util.List<ProductImageDTO> getImages() {
        return images;
    }

    public void setImages(java.util.List<ProductImageDTO> images) {
        this.images = images;
    }
}
