package com.musicshop.service.cart;

import com.musicshop.model.cart.Cart;
import com.musicshop.model.cart.CartDetail;
import com.musicshop.model.product.Product;
import com.musicshop.model.user.User;
import com.musicshop.repository.cart.CartDetailRepository;
import com.musicshop.repository.cart.CartRepository;
import com.musicshop.repository.product.ProductRepository;
import com.musicshop.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public CartService(CartRepository cartRepository, CartDetailRepository cartDetailRepository,
            ProductRepository productRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Cart createNewCart(User user) {
        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setDateCreated(LocalDateTime.now());
        return cartRepository.save(newCart);
    }

    public CartDetail addProductToCart(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> createNewCart(user));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (quantity > product.getQuantityAvailable()) {
            throw new RuntimeException("Not enough quantity available");
        }

        // Update product quantity
        product.setQuantityAvailable(product.getQuantityAvailable() - quantity);
        productRepository.save(product);

        CartDetail cartDetail = new CartDetail();
        cartDetail.setCart(cart);
        cartDetail.setProduct(product);
        cartDetail.setQuantity(quantity);
        return cartDetailRepository.save(cartDetail);
    }

    public Optional<CartDetail> getCartDetail(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();
        return cartDetailRepository.findByCartAndProduct(cart, product);
    }

    public List<CartDetail> listCartDetails(Long cartId) {
        return cartRepository.findById(cartId).map(cart -> {
            List<CartDetail> cartDetails = cartDetailRepository.findAll();
            cartDetails.removeIf(detail -> !detail.getCart().getId().equals(cartId));
            return cartDetails;
        }).orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    public CartDetail updateCartDetail(Long detailId, int newQuantity) {
        CartDetail cartDetail = cartDetailRepository.findById(detailId).orElseThrow();
        cartDetail.setQuantity(newQuantity);
        return cartDetailRepository.save(cartDetail);
    }

    public void deleteCartDetail(Long detailId) {
        CartDetail detail = cartDetailRepository.findById(detailId)
                .orElseThrow(() -> new RuntimeException("Cart detail not found"));

        Product product = detail.getProduct();
        product.setQuantityAvailable(product.getQuantityAvailable() + detail.getQuantity());
        productRepository.save(product);

        cartDetailRepository.delete(detail);
    }

    public void clearCart(Long cartId) {
        List<CartDetail> details = listCartDetails(cartId);
        for (CartDetail detail : details) {
            Product product = detail.getProduct();
            product.setQuantityAvailable(product.getQuantityAvailable() + detail.getQuantity());
            productRepository.save(product);
        }
        cartDetailRepository.deleteAll(details);
    }
}
