package com.musicshop.dto.category;

import java.util.List;

public record CategoryDTO(
        Long id,
        String name,
        String slug,
        String description,
        Long productCount,
        List<CategoryDTO> subCategories) {

    public CategoryDTO(Long id, String name, String slug, Long productCount) {
        this(id, name, slug, null, productCount, List.of());
    }

    public CategoryDTO(Long id, String name, String slug) {
        this(id, name, slug, null, 0L, List.of());
    }

    public CategoryDTO(Long id, String name, String slug, String description, Long productCount) {
        this(id, name, slug, description, productCount, List.of());
    }

    public CategoryDTO {
        subCategories = (subCategories == null) ? List.of() : List.copyOf(subCategories);
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

    public Long getProductCount() {
        return productCount;
    }

    public List<CategoryDTO> getSubCategories() {
        return subCategories;
    }

    public CategoryDTO withDescription(String value) {
        return new CategoryDTO(id, name, slug, value, productCount, subCategories);
    }

    public CategoryDTO withProductCount(Long value) {
        return new CategoryDTO(id, name, slug, description, value, subCategories);
    }

    public CategoryDTO withSubCategories(List<CategoryDTO> value) {
        return new CategoryDTO(id, name, slug, description, productCount, value);
    }
}
