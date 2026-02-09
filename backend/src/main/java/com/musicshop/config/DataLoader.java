package com.musicshop.config;

import org.springframework.boot.CommandLineRunner;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.musicshop.model.category.Category;
import com.musicshop.model.user.User;
import com.musicshop.model.product.Product;
import com.musicshop.model.cart.Cart;
import com.musicshop.repository.category.CategoryRepository;
import com.musicshop.repository.user.UserRepository;
import com.musicshop.repository.product.ProductRepository;
import com.musicshop.repository.cart.CartRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;

    @Override
    public void run(String... args) throws Exception {
        // This will be the only user for now
        if (userRepository.count() == 0) {
            User user = new User();
            user.setFirstName("John");
            user.setLastName("Doe");
            user.setEmail("john.doe@example.com");
            user.setPhoneNumber("1234567890");
            userRepository.save(user);

            // Create a cart for the user
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setDateCreated(LocalDateTime.now());
            cartRepository.save(cart);
        }

        // Categories are already seeded by CategoryServiceImpl
        // We just need to add some sample products if they don't exist

        if (productRepository.count() == 0) {
            Category guitars = categoryRepository.findByCategoryName("Guitars")
                    .orElseGet(() -> {
                        Category c = new Category();
                        c.setCategoryName("Guitars");
                        return categoryRepository.save(c);
                    });

            Product product1 = new Product();
            product1.setName("Martin LX1E");
            product1.setDescription("Portable and sounds great");
            product1.setPrice(new BigDecimal("550.99"));
            product1.setQuantityAvailable(10);
            product1.setCategory(guitars);
            productRepository.save(product1);

            Product product2 = new Product();
            product2.setName("Fender Player Stratocaster");
            product2.setDescription("Classic sound and look");
            product2.setPrice(new BigDecimal("649.99"));
            product2.setQuantityAvailable(8);
            product2.setCategory(guitars);
            productRepository.save(product2);
        }
    }
}