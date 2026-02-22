package com.musicshop.application.cart;

import com.musicshop.dto.cart.CartItemDTO;
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
        Long userId = userService.findUserIdByEmail(email);
        cartService.addProductToCart(userId, productId, quantity);
    }

    @PreAuthorize("isAuthenticated()")
    public List<CartItemDTO> listCartDetails(String email) {
        Long userId = userService.findUserIdByEmail(email);
        return cartService.listCartItemDTOsForUser(userId);
    }

    @PreAuthorize("isAuthenticated()")
    public void clearCart(String email) {
        Long userId = userService.findUserIdByEmail(email);
        cartService.clearCartForUserId(userId);
    }

    public CartItemDTO updateCartDetail(Long detailId, int newQuantity) {
        return cartService.updateCartItemDTO(detailId, newQuantity);
    }

    public void deleteCartDetail(Long detailId) {
        cartService.deleteCartDetail(detailId);
    }
}
