package com.musicshop.service.category;

import com.musicshop.dto.category.CategoryDTO;
import com.musicshop.dto.category.CreateCategoryRequest;
import com.musicshop.mapper.CategoryMapper;
import com.musicshop.model.category.Category;
import com.musicshop.repository.category.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<CategoryDTO> findAllProperties() {
        // Fetch parent categories with counts
        List<CategoryDTO> parents = categoryRepository.findAllWithProductCount();

        // For each parent, fetch subcategories with counts and return an enriched immutable DTO.
        return parents.stream()
                .map(parent -> categoryRepository.findBySlug(parent.getSlug())
                        .map(parentEntity -> {
                            List<CategoryDTO> subs = categoryRepository.findSubcategoriesWithProductCount(parentEntity);
                            long totalCount = subs.stream().mapToLong(CategoryDTO::getProductCount).sum();
                            return parent
                                    .withSubCategories(subs)
                                    .withDescription(parentEntity.getDescription())
                                    .withProductCount(totalCount);
                        })
                        .orElse(parent))
                .toList();
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public CategoryDTO createCategory(CreateCategoryRequest request, Long parentId) {
        Category category = new Category();
        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        category.setIconUrl(request.getIconUrl());
        category.setSlug(request.getSlug());

        if (parentId != null) {
            categoryRepository.findById(parentId).ifPresent(category::setParentCategory);
        }

        if (category.getSlug() == null || category.getSlug().isBlank()) {
            category.setSlug(category.getCategoryName().toLowerCase().replaceAll("[^a-z0-9]+", "-"));
        }
        Category saved = categoryRepository.save(category);
        return categoryMapper.toCategoryDTO(saved).withProductCount(0L);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
