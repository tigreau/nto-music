package com.musicshop.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musicshop.model.product.ProductCondition;

import java.math.BigDecimal;

public class SimpleProductDTO {
    @JsonProperty("id")
    protected Long id;
    @JsonProperty("name")
    protected String name;
    @JsonProperty("slug")
    protected String slug;
    @JsonProperty("price")
    protected BigDecimal price;
    @JsonProperty("categoryName")
    protected String categoryName;
    @JsonProperty("brandName")
    protected String brandName;
    @JsonProperty("condition")
    protected ProductCondition condition;
    @JsonProperty("thumbnailUrl")
    protected String thumbnailUrl;
    @JsonProperty("isPromoted")
    protected boolean isPromoted;

    protected SimpleProductDTO() {
    }

    public static class Builder {
        protected Long id;
        protected String name;
        protected String slug;
        protected BigDecimal price;
        protected String categoryName;
        protected String brandName;
        protected ProductCondition condition;
        protected String thumbnailUrl;
        protected boolean isPromoted;

        public Builder() {
        }

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

        public Builder price(BigDecimal price) {
            this.price = price;
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

        public Builder thumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public Builder isPromoted(boolean isPromoted) {
            this.isPromoted = isPromoted;
            return this;
        }

        public SimpleProductDTO build() {
            SimpleProductDTO dto = new SimpleProductDTO();
            dto.id = this.id;
            dto.name = this.name;
            dto.slug = this.slug;
            dto.price = this.price;
            dto.categoryName = this.categoryName;
            dto.brandName = this.brandName;
            dto.condition = this.condition;
            dto.thumbnailUrl = this.thumbnailUrl;
            dto.isPromoted = this.isPromoted;
            return dto;
        }
    }
}
