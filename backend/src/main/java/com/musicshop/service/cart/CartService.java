package com.musicshop.service.cart;

import com.musicshop.model.cart.Cart;
import com.musicshop.model.cart.CartDetail;
import com.musicshop.model.product.Product;
import com.musicshop.model.user.User;
import com.musicshop.repository.cart.CartDetailRepository;
import com.musicshop.repository.cart.CartRepository;
import com.musicshop.repository.product.ProductRepository;
import com.musicshop.repository.user.UserRepository;
import com.musicshop.dto.cart.CartItemDTO;
import com.musicshop.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @Autowired
    public CartService(CartRepository cartRepository, CartDetailRepository cartDetailRepository,
            ProductRepository productRepository, UserRepository userRepository,
            CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
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

    @Transactional(readOnly = true)
    public Optional<CartItemDTO> getCartItemDTO(Long cartId, Long productId) {
        return getCartDetail(cartId, productId).map(cartMapper::toCartItemDTO);
    }

    public List<CartDetail> listCartDetails(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return cartDetailRepository.findByCart(cart);
    }

    @Transactional(readOnly = true)
    public List<CartItemDTO> listCartItemDTOs(Long cartId) {
        List<CartDetail> details = listCartDetails(cartId);
        return cartMapper.toCartItemDTOs(details);
    }

    public CartDetail updateCartDetail(Long detailId, int newQuantity) {
        CartDetail cartDetail = cartDetailRepository.findById(detailId).orElseThrow();
        cartDetail.setQuantity(newQuantity);
        return cartDetailRepository.save(cartDetail);
    }

    @Transactional
    public CartItemDTO updateCartItemDTO(Long detailId, int newQuantity) {
        CartDetail updatedDetail = updateCartDetail(detailId, newQuantity);
        return cartMapper.toCartItemDTO(updatedDetail);
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
