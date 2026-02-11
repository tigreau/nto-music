package com.musicshop.controller.category;

import com.musicshop.dto.category.CategoryDTO;
import com.musicshop.dto.review.CategoryReviewsDTO;
import com.musicshop.model.category.Category;
import com.musicshop.service.category.CategoryService;
import com.musicshop.service.review.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ReviewService reviewService;

    @Autowired
    public CategoryController(CategoryService categoryService, ReviewService reviewService) {
        this.categoryService = categoryService;
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAllProperties());
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(
            @RequestBody Category category,
            @RequestParam(required = false) Long parentId) {

        if (parentId != null) {
            categoryService.findById(parentId).ifPresent(category::setParentCategory);
        }

        Category createdCategory = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    /**
     * Get reviews for a category by slug.
     * Reviews are shown at the bottom of the category page, each linked to the
     * product purchased.
     *
     * GET /api/categories/{slug}/reviews?page=0&size=10
     */
    @GetMapping("/{slug}/reviews")
    public ResponseEntity<CategoryReviewsDTO> getCategoryReviews(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        CategoryReviewsDTO reviews = reviewService.getReviewsByCategory(slug, page, size);
        return ResponseEntity.ok(reviews);
    }
}
