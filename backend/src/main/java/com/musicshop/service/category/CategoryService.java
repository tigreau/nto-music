package com.musicshop.service.category;

import com.musicshop.dto.category.CreateCategoryRequest;
import com.musicshop.dto.category.CategoryDTO;
import com.musicshop.model.category.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<CategoryDTO> findAllProperties();

    List<Category> findAll();

    CategoryDTO createCategory(CreateCategoryRequest request, Long parentId);

    Optional<Category> findById(Long id);

    void deleteCategory(Long id);
}
