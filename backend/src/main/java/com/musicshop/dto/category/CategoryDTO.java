package com.musicshop.dto.category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDTO {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private Long productCount;
    private List<CategoryDTO> subCategories = new ArrayList<>();

    // No-arg constructor
    public CategoryDTO() {
    }

    // Constructor for JPQL query (parent categories)
    public CategoryDTO(Long id, String name, String slug, Long productCount) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.productCount = productCount;
    }

    // Simplified constructor for Cart
    public CategoryDTO(Long id, String name, String slug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
    }

    // Full constructor
    public CategoryDTO(Long id, String name, String slug, String description, Long productCount) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.productCount = productCount;
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

    public Long getProductCount() {
        return productCount;
    }

    public void setProductCount(Long productCount) {
        this.productCount = productCount;
    }

    public List<CategoryDTO> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<CategoryDTO> subCategories) {
        this.subCategories = subCategories;
    }
}
