package com.musicshop.dto.review;

import java.time.LocalDateTime;

public class ReviewDTO {
    private Long id;
    private String userName;
    private int rating;
    private String comment;
    private String productName;
    private String productThumbnailUrl;
    private boolean verifiedPurchase;
    private LocalDateTime datePosted;

    public ReviewDTO(Long id, String userName, int rating, String comment,
            String productName, String productThumbnailUrl,
            boolean verifiedPurchase, LocalDateTime datePosted) {
        this.id = id;
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.productName = productName;
        this.productThumbnailUrl = productThumbnailUrl;
        this.verifiedPurchase = verifiedPurchase;
        this.datePosted = datePosted;
    }

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
