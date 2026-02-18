package com.musicshop.application.category;

import com.musicshop.dto.category.CategoryDTO;
import com.musicshop.dto.category.CreateCategoryRequest;
import com.musicshop.dto.review.CategoryReviewsDTO;
import com.musicshop.service.category.CategoryService;
import com.musicshop.service.review.ReviewService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryUseCase {

    private final CategoryService categoryService;
    private final ReviewService reviewService;

    public CategoryUseCase(CategoryService categoryService, ReviewService reviewService) {
        this.categoryService = categoryService;
        this.reviewService = reviewService;
    }

    public List<CategoryDTO> listCategories() {
        return categoryService.findAllProperties();
    }

    public CategoryDTO createCategory(CreateCategoryRequest request, Long parentId) {
        return categoryService.createCategory(request, parentId);
    }

    public CategoryReviewsDTO getCategoryReviews(String slug, int page, int size) {
        return reviewService.getReviewsByCategory(slug, page, size);
    }
}
