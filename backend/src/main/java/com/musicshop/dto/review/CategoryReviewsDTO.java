package com.musicshop.dto.review;

import java.util.List;

public record CategoryReviewsDTO(
        String categoryName,
        Double averageRating,
        long reviewCount,
        List<ReviewDTO> reviews) {

    public CategoryReviewsDTO {
        reviews = (reviews == null) ? List.of() : List.copyOf(reviews);
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public long getReviewCount() {
        return reviewCount;
    }

    public List<ReviewDTO> getReviews() {
        return reviews;
    }
}
