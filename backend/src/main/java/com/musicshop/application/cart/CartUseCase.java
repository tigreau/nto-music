package com.musicshop.application.cart;

import com.musicshop.dto.cart.CartItemDTO;
import com.musicshop.model.cart.Cart;
import com.musicshop.model.user.User;
import org.springframework.security.access.prepost.PreAuthorize;
import com.musicshop.service.cart.CartService;
import com.musicshop.service.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartUseCase {

    private final CartService cartService;
    private final UserService userService;

    public CartUseCase(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @PreAuthorize("isAuthenticated()")
    public void addProduct(String email, Long productId, int quantity) {
        User user = userService.findByEmail(email);
        cartService.addProductToCart(user.getId(), productId, quantity);
    }

    @PreAuthorize("isAuthenticated()")
    public List<CartItemDTO> listCartDetails(String email) {
        User user = userService.findByEmail(email);
        Cart cart = cartService.getCartForUser(user);
        return cartService.listCartItemDTOs(cart.getId());
    }

    @PreAuthorize("isAuthenticated()")
    public void clearCart(String email) {
        User user = userService.findByEmail(email);
        cartService.clearCartForUser(user);
    }

    public CartItemDTO updateCartDetail(Long detailId, int newQuantity) {
        return cartService.updateCartItemDTO(detailId, newQuantity);
    }

    public void deleteCartDetail(Long detailId) {
        cartService.deleteCartDetail(detailId);
    }
}
