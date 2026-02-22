package com.musicshop.dto.review;

import java.time.LocalDateTime;

public record ReviewDTO(
        Long id,
        String userName,
        int rating,
        String comment,
        String productName,
        String productThumbnailUrl,
        boolean verifiedPurchase,
        LocalDateTime datePosted) {

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductThumbnailUrl() {
        return productThumbnailUrl;
    }

    public boolean isVerifiedPurchase() {
        return verifiedPurchase;
    }

    public LocalDateTime getDatePosted() {
        return datePosted;
    }
}
