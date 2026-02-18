package com.musicshop.repository.review;

import com.musicshop.model.product.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByProductId(Long productId);

    Page<Review> findByCategoryIdIn(List<Long> categoryIds, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.category.id IN :categoryIds")
    Double findAverageRatingByCategoryIdIn(@Param("categoryIds") List<Long> categoryIds);

    long countByCategoryIdIn(List<Long> categoryIds);
}
