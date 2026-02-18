package com.musicshop.controller.category;

import com.musicshop.application.category.CategoryUseCase;
import com.musicshop.dto.category.CategoryDTO;
import com.musicshop.dto.category.CreateCategoryRequest;
import com.musicshop.dto.review.CategoryReviewsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryUseCase categoryUseCase;

    @Autowired
    public CategoryController(CategoryUseCase categoryUseCase) {
        this.categoryUseCase = categoryUseCase;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryUseCase.listCategories());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDTO> createCategory(
            @Valid @RequestBody CreateCategoryRequest request,
            @RequestParam(required = false) Long parentId) {

        CategoryDTO createdCategory = categoryUseCase.createCategory(request, parentId);
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
        CategoryReviewsDTO reviews = categoryUseCase.getCategoryReviews(slug, page, size);
        return ResponseEntity.ok(reviews);
    }
}
