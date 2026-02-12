package com.musicshop.data.seeder;

import com.musicshop.model.product.Product;
import com.musicshop.model.product.Review;
import com.musicshop.model.user.User;
import com.musicshop.repository.product.ProductRepository;
import com.musicshop.repository.review.ReviewRepository;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ReviewSeeder implements DataSeeder {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserSeeder userSeeder;

    public ReviewSeeder(ReviewRepository reviewRepository, ProductRepository productRepository, UserSeeder userSeeder) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userSeeder = userSeeder;
    }

    @Override
    @Transactional
    public void seed() {
        if (reviewRepository.count() > 0)
            return;

        User user = userSeeder.getDefaultUser();
        if (user == null) {
            return;
        }

        // Get some products to attach reviews to
        productRepository.findByName("Fender Player Stratocaster").ifPresent(product -> {
            createReview(user, product, 5,
                    "Incredible guitar for the price. The pickups sound amazing and the neck is super comfortable.");
            createReview(user, product, 4,
                    "Great quality, minor cosmetic mark on the body but plays beautifully.");
        });

        productRepository.findByName("Gibson Les Paul Standard '50s").ifPresent(product -> createReview(user,
                product,
                5, "The tone is unreal. Worth every penny. Best guitar I've ever played."));

        productRepository.findByName("Yamaha P-125 Digital Piano")
                .ifPresent(product -> createReview(user, product, 4,
                        "Weighted keys feel realistic. Great for practicing at home."));

        productRepository.findByName("Roland TD-17KVX V-Drums")
                .ifPresent(product -> createReview(user, product, 5,
                        "Best e-drums in this price range. Mesh heads feel natural."));
    }

    private void createReview(User user, Product product, int rating, String comment) {
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setCategory(product.getCategory());
        review.setRating(rating);
        review.setComment(comment);
        review.setVerifiedPurchase(true);
        reviewRepository.save(review);
    }
}
