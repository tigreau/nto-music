package com.musicshop.repository.cart;

import com.musicshop.model.cart.Cart;
import com.musicshop.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User customer);

    Optional<Cart> findByUserId(Long userId);
}