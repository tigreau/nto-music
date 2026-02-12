package com.musicshop.service.category;

import com.musicshop.dto.category.CategoryDTO;
import com.musicshop.model.category.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<CategoryDTO> findAllProperties();

    List<Category> findAll();

    Category createCategory(Category category, Long parentId);

    Optional<Category> findById(Long id);

    void deleteCategory(Long id);
}
