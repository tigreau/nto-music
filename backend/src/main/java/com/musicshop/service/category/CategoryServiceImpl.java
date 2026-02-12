package com.musicshop.service.category;

import com.musicshop.dto.category.CategoryDTO;
import com.musicshop.model.category.Category;
import com.musicshop.repository.category.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryDTO> findAllProperties() {
        // Fetch parent categories with counts
        List<CategoryDTO> parents = categoryRepository.findAllWithProductCount();

        // For each parent, fetch subcategories with counts
        for (CategoryDTO parent : parents) {
            categoryRepository.findBySlug(parent.getSlug()).ifPresent(parentEntity -> {
                List<CategoryDTO> subs = categoryRepository.findSubcategoriesWithProductCount(parentEntity);
                parent.setSubCategories(subs);

                // Set description on parent DTO
                parent.setDescription(parentEntity.getDescription());

                // Aggregate: parent productCount = sum of subcategory counts
                long totalCount = subs.stream().mapToLong(CategoryDTO::getProductCount).sum();
                parent.setProductCount(totalCount);
            });
        }

        return parents;
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category createCategory(Category category, Long parentId) {
        if (parentId != null) {
            categoryRepository.findById(parentId).ifPresent(category::setParentCategory);
        }

        if (category.getSlug() == null || category.getSlug().isBlank()) {
            category.setSlug(category.getCategoryName().toLowerCase().replaceAll("[^a-z0-9]+", "-"));
        }
        return categoryRepository.save(category);
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
