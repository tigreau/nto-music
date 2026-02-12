package com.musicshop.repository.cart;

import com.musicshop.model.cart.CartDetail;
import com.musicshop.model.cart.Cart;
import com.musicshop.model.product.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
    Optional<CartDetail> findByCartAndProduct(Cart cart, Product product);

    @EntityGraph(attributePaths = { "product" })
    List<CartDetail> findByCart(Cart cart);

    List<CartDetail> findByProductId(Long productId);
}
