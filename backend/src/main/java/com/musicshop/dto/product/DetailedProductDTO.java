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

    private DetailedProductDTO() {
    }

    // Getters for JSON serialization
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

    public boolean isPromoted() {
        return isPromoted;
    }

    public List<ProductImageDTO> getImages() {
        return images;
    }

    public static class ProductImageDTO {
        private Long id;
        private String url;
        private String altText;
        private boolean isPrimary;

        public ProductImageDTO(Long id, String url, String altText, boolean isPrimary) {
            this.id = id;
            this.url = url;
            this.altText = altText;
            this.isPrimary = isPrimary;
        }

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
    }

    public static class Builder {
        private Long id;
        private String name;
        private String slug;
        private String description;
        private BigDecimal price;
        private String categoryName;
        private String brandName;
        private Integer quantityAvailable;
        private ProductCondition condition;
        private String conditionNotes;
        private boolean isPromoted;
        private List<ProductImageDTO> images;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder slug(String slug) {
            this.slug = slug;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder quantityAvailable(Integer quantityAvailable) {
            this.quantityAvailable = quantityAvailable;
            return this;
        }

        public Builder categoryName(String categoryName) {
            this.categoryName = categoryName;
            return this;
        }

        public Builder brandName(String brandName) {
            this.brandName = brandName;
            return this;
        }

        public Builder condition(ProductCondition condition) {
            this.condition = condition;
            return this;
        }

        public Builder conditionNotes(String conditionNotes) {
            this.conditionNotes = conditionNotes;
            return this;
        }

        public Builder isPromoted(boolean isPromoted) {
            this.isPromoted = isPromoted;
            return this;
        }

        public Builder images(List<ProductImageDTO> images) {
            this.images = images;
            return this;
        }

        public DetailedProductDTO build() {
            DetailedProductDTO dto = new DetailedProductDTO();
            dto.id = this.id;
            dto.name = this.name;
            dto.slug = this.slug;
            dto.description = this.description;
            dto.price = this.price;
            dto.quantityAvailable = this.quantityAvailable;
            dto.categoryName = this.categoryName;
            dto.brandName = this.brandName;
            dto.condition = this.condition;
            dto.conditionNotes = this.conditionNotes;
            dto.isPromoted = this.isPromoted;
            dto.images = this.images;
            return dto;
        }
    }
}
