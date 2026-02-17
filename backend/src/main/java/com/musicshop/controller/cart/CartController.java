package com.musicshop.controller.cart;

import com.musicshop.dto.cart.CartItemDTO;
import com.musicshop.model.cart.Cart;
import com.musicshop.model.user.User;
import com.musicshop.service.cart.CartService;
import com.musicshop.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @Autowired
    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @PostMapping("/my/products/{productId}")
    public ResponseEntity<?> addProductToMyCart(@PathVariable Long productId,
            @RequestParam int quantity,
            Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        cartService.addProductToCart(user.getId(), productId, quantity);
        return ResponseEntity.ok().body("Product added to cart");
    }

    @GetMapping("/my/details")
    public ResponseEntity<List<CartItemDTO>> listMyCartDetails(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        Cart cart = cartService.getCartForUser(user);
        return ResponseEntity.ok(cartService.listCartItemDTOs(cart.getId()));
    }

    @DeleteMapping("/my/clear")
    public ResponseEntity<?> clearMyCart(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        cartService.clearCartForUser(user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/details/{detailId}")
    public ResponseEntity<CartItemDTO> updateCartDetail(@PathVariable Long detailId, @RequestParam int newQuantity) {
        CartItemDTO updatedCartDetail = cartService.updateCartItemDTO(detailId, newQuantity);
        return ResponseEntity.ok(updatedCartDetail);
    }

    @DeleteMapping("/details/{detailId}")
    public ResponseEntity<?> deleteCartDetail(@PathVariable Long detailId) {
        cartService.deleteCartDetail(detailId);
        return ResponseEntity.ok().build();
    }
}
