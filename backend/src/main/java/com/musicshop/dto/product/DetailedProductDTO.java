package com.musicshop.dto.product;

import com.musicshop.model.product.ProductCondition;

import java.math.BigDecimal;
import java.util.List;

public class DetailedProductDTO {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private BigDecimal price;
    private Integer quantityAvailable;
    private String categoryName;
    private String brandName;
    private ProductCondition condition;
    private String conditionNotes;
    private boolean isPromoted;
    private List<ProductImageDTO> images;

    public DetailedProductDTO() {
    }

    public DetailedProductDTO(Long id, String name, String slug, String description, BigDecimal price,
            Integer quantityAvailable, String categoryName, String brandName, ProductCondition condition,
            String conditionNotes, boolean isPromoted, List<ProductImageDTO> images) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.price = price;
        this.quantityAvailable = quantityAvailable;
        this.categoryName = categoryName;
        this.brandName = brandName;
        this.condition = condition;
        this.conditionNotes = conditionNotes;
        this.isPromoted = isPromoted;
        this.images = images;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(Integer quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
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

    public String getConditionNotes() {
        return conditionNotes;
    }

    public void setConditionNotes(String conditionNotes) {
        this.conditionNotes = conditionNotes;
    }

    public boolean isPromoted() {
        return isPromoted;
    }

    public void setPromoted(boolean promoted) {
        isPromoted = promoted;
    }

    public List<ProductImageDTO> getImages() {
        return images;
    }

    public void setImages(List<ProductImageDTO> images) {
        this.images = images;
    }

    public static class ProductImageDTO {
        private Long id;
        private String url;
        private String altText;
        private boolean isPrimary;

        public ProductImageDTO() {
        }

        public ProductImageDTO(Long id, String url, String altText, boolean isPrimary) {
            this.id = id;
            this.url = url;
            this.altText = altText;
            this.isPrimary = isPrimary;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getAltText() {
            return altText;
        }

        public void setAltText(String altText) {
            this.altText = altText;
        }

        public boolean isPrimary() {
            return isPrimary;
        }

        public void setPrimary(boolean primary) {
            isPrimary = primary;
        }
    }
}
