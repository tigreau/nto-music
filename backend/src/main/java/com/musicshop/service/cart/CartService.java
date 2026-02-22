package com.musicshop.service.cart;

import com.musicshop.exception.InsufficientStockException;
import com.musicshop.exception.ResourceNotFoundException;
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

    @Transactional
    public CartDetail addProductToCart(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> createNewCart(user));

        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (quantity > product.getQuantityAvailable()) {
            throw new InsufficientStockException("Not enough quantity available");
        }

        product.setQuantityAvailable(product.getQuantityAvailable() - quantity);
        productRepository.save(product);

        CartDetail cartDetail = new CartDetail();
        cartDetail.setCart(cart);
        cartDetail.setProduct(product);
        cartDetail.setQuantity(quantity);
        return cartDetailRepository.save(cartDetail);
    }

    public Optional<CartDetail> getCartDetail(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return cartDetailRepository.findByCartAndProduct(cart, product);
    }

    @Transactional(readOnly = true)
    public Optional<CartItemDTO> getCartItemDTO(Long cartId, Long productId) {
        return getCartDetail(cartId, productId).map(cartMapper::toCartItemDTO);
    }

    public List<CartDetail> listCartDetails(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        return cartDetailRepository.findByCart(cart);
    }

    @Transactional(readOnly = true)
    public List<CartItemDTO> listCartItemDTOs(Long cartId) {
        List<CartDetail> details = listCartDetails(cartId);
        return cartMapper.toCartItemDTOs(details);
    }

    public CartDetail updateCartDetail(Long detailId, int newQuantity) {
        CartDetail cartDetail = cartDetailRepository.findById(detailId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart detail not found"));
        cartDetail.setQuantity(newQuantity);
        return cartDetailRepository.save(cartDetail);
    }

    @Transactional
    public CartItemDTO updateCartItemDTO(Long detailId, int newQuantity) {
        CartDetail updatedDetail = updateCartDetail(detailId, newQuantity);
        return cartMapper.toCartItemDTO(updatedDetail);
    }

    @Transactional
    public void deleteCartDetail(Long detailId) {
        CartDetail detail = cartDetailRepository.findById(detailId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart detail not found"));

        Product product = detail.getProduct();
        product.setQuantityAvailable(product.getQuantityAvailable() + detail.getQuantity());
        productRepository.save(product);

        cartDetailRepository.delete(detail);
    }

    @Transactional
    public void clearCart(Long cartId) {
        List<CartDetail> details = listCartDetails(cartId);
        for (CartDetail detail : details) {
            Product product = detail.getProduct();
            product.setQuantityAvailable(product.getQuantityAvailable() + detail.getQuantity());
            productRepository.save(product);
        }
        cartDetailRepository.deleteAll(details);
    }

    public Cart getCartForUser(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> createNewCart(user));
    }

    public void clearCartForUser(User user) {
        Cart cart = cartRepository.findByUser(user).orElse(null);
        if (cart != null) {
            clearCart(cart.getId());
        }
    }

    @Transactional(readOnly = true)
    public List<CartItemDTO> listCartItemDTOsForUser(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        return listCartItemDTOs(cart.getId());
    }

    public void clearCartForUserId(Long userId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> clearCart(cart.getId()));
    }
}
