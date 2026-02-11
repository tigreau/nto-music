package com.musicshop.service.review;

import com.musicshop.dto.review.CategoryReviewsDTO;
import com.musicshop.dto.review.ReviewDTO;
import com.musicshop.model.category.Category;
import com.musicshop.model.product.Review;
import com.musicshop.repository.category.CategoryRepository;
import com.musicshop.repository.review.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, CategoryRepository categoryRepository) {
        this.reviewRepository = reviewRepository;
        this.categoryRepository = categoryRepository;
    }

    public CategoryReviewsDTO getReviewsByCategory(String categorySlug, int page, int size) {
        Category category = categoryRepository.findBySlug(categorySlug)
                .orElseThrow(() -> new RuntimeException("Category not found: " + categorySlug));

        // Collect IDs: current category + all unique subcategory IDs
        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(category.getId());
        if (category.getSubCategories() != null) {
            for (Category sub : category.getSubCategories()) {
                categoryIds.add(sub.getId());
            }
        }

        Page<Review> reviewPage = reviewRepository.findByCategoryIdIn(
                categoryIds,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "datePosted")));

        Double avgRating = reviewRepository.findAverageRatingByCategoryIdIn(categoryIds);
        long totalCount = reviewRepository.countByCategoryIdIn(categoryIds);

        return new CategoryReviewsDTO(
                category.getCategoryName(),
                avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : null,
                totalCount,
                reviewPage.getContent().stream()
                        .map(this::toReviewDTO)
                        .toList());
    }

    private ReviewDTO toReviewDTO(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getUser().getFirstName() + " " + review.getUser().getLastName().charAt(0) + ".",
                review.getRating(),
                review.getComment(),
                review.getProduct().getName(),
                review.getProduct().getThumbnailUrl(),
                review.isVerifiedPurchase(),
                review.getDatePosted());
    }

    public Review createReview(Review review) {
        // Auto-set the category from the product
        if (review.getProduct() != null && review.getProduct().getCategory() != null) {
            review.setCategory(review.getProduct().getCategory());
        }
        return reviewRepository.save(review);
    }
}
