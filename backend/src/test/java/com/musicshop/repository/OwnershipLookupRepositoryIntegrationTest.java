package com.musicshop.repository;

import com.musicshop.model.cart.Cart;
import com.musicshop.model.cart.CartDetail;
import com.musicshop.model.category.Category;
import com.musicshop.model.product.Product;
import com.musicshop.model.product.ProductCondition;
import com.musicshop.model.user.Notification;
import com.musicshop.model.user.NotificationType;
import com.musicshop.model.user.User;
import com.musicshop.model.user.UserRole;
import com.musicshop.repository.cart.CartDetailRepository;
import com.musicshop.repository.user.NotificationRepository;
import com.musicshop.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OwnershipLookupRepositoryIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartDetailRepository cartDetailRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void userRepository_findsOwnerByEmail() {
        User owner = persistUser("owner@example.com");

        Optional<User> found = userRepository.findByEmail("owner@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(owner.getId());
    }

    @Test
    void cartDetailRepository_lookup_preservesOwnershipLink() {
        User owner = persistUser("cart-owner@example.com");
        Product product = persistProduct("Cart Product", "cart-product");

        Cart cart = new Cart();
        cart.setUser(owner);
        entityManager.persist(cart);

        CartDetail detail = new CartDetail();
        detail.setCart(cart);
        detail.setProduct(product);
        detail.setQuantity(2);
        entityManager.persist(detail);
        entityManager.flush();
        entityManager.clear();

        CartDetail found = cartDetailRepository.findById(detail.getId()).orElseThrow();

        assertThat(found.getCart().getUser().getEmail()).isEqualTo("cart-owner@example.com");
    }

    @Test
    void notificationRepository_lookup_preservesOwnershipLink() {
        User owner = persistUser("notification-owner@example.com");

        Notification notification = new Notification();
        notification.setUser(owner);
        notification.setMessage("Order shipped");
        notification.setType(NotificationType.ORDER_SHIPPED);
        entityManager.persist(notification);
        entityManager.flush();
        entityManager.clear();

        Notification found = notificationRepository.findById(notification.getId()).orElseThrow();

        assertThat(found.getUser().getEmail()).isEqualTo("notification-owner@example.com");
    }

    private User persistUser(String email) {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail(email);
        user.setPassword("x");
        user.setRole(UserRole.CUSTOMER);
        entityManager.persist(user);
        return user;
    }

    private Product persistProduct(String name, String slug) {
        Category category = new Category();
        category.setCategoryName("Guitars");
        category.setSlug("guitars");
        entityManager.persist(category);

        Product product = new Product();
        product.setName(name);
        product.setDescription("desc");
        product.setPrice(BigDecimal.valueOf(1000));
        product.setQuantityAvailable(5);
        product.setCategory(category);
        product.setCondition(ProductCondition.NEW);
        product.setSlug(slug);
        entityManager.persist(product);
        return product;
    }
}
