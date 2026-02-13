package com.musicshop.mapper;

import com.musicshop.dto.review.CategoryReviewsDTO;
import com.musicshop.dto.review.ReviewDTO;
import com.musicshop.model.product.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "userName", expression = "java(review.getUser().getFirstName() + \" \" + review.getUser().getLastName().charAt(0) + \".\")")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productThumbnailUrl", source = "product.thumbnailUrl")
    ReviewDTO toReviewDTO(Review review);

    default CategoryReviewsDTO toCategoryReviewsDTO(String categoryName, Double averageRating, long totalCount,
            List<ReviewDTO> reviews) {
        return new CategoryReviewsDTO(categoryName, averageRating, totalCount, reviews);
    }
}
