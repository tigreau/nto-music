package com.musicshop.controller.cart;

import com.musicshop.dto.cart.CartItemDTO;
import com.musicshop.model.cart.Cart;

import com.musicshop.model.user.User;
import com.musicshop.repository.cart.CartRepository;
import com.musicshop.repository.user.UserRepository;
import com.musicshop.service.cart.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/carts")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    @Autowired
    public CartController(CartService cartService,
            UserRepository userRepository, CartRepository cartRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
    }

    // ─── Authenticated "my cart" endpoints ────────────────────────────

    @PostMapping("/my/products/{productId}")
    public ResponseEntity<?> addProductToMyCart(@PathVariable Long productId,
            @RequestParam int quantity,
            Authentication authentication) {
        try {
            User user = getAuthenticatedUser(authentication);
            cartService.addProductToCart(user.getId(), productId, quantity);
            return ResponseEntity.ok().body("Product added to cart");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my/details")
    public ResponseEntity<List<CartItemDTO>> listMyCartDetails(Authentication authentication) {
        try {
            User user = getAuthenticatedUser(authentication);
            Cart cart = cartRepository.findByUser(user)
                    .orElseGet(() -> cartService.createNewCart(user));
            return ResponseEntity.ok(cartService.listCartItemDTOs(cart.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/my/clear")
    public ResponseEntity<?> clearMyCart(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        Cart cart = cartRepository.findByUser(user).orElse(null);
        if (cart != null) {
            cartService.clearCart(cart.getId());
        }
        return ResponseEntity.ok().build();
    }

    private User getAuthenticatedUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ─── Legacy endpoints (kept for backwards compat) ────────────────

    @PostMapping("/{userId}/products/{productId}")
    public ResponseEntity<?> addProductToCart(@PathVariable Long userId, @PathVariable Long productId,
            @RequestParam int quantity) {
        try {
            cartService.addProductToCart(userId, productId, quantity);
            return ResponseEntity.ok().body("Product added to cart");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{cartId}/products/{productId}")
    public ResponseEntity<CartItemDTO> getCartDetail(@PathVariable Long cartId, @PathVariable Long productId) {
        return cartService.getCartItemDTO(cartId, productId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{cartId}/details")
    public ResponseEntity<List<CartItemDTO>> listCartDetails(@PathVariable Long cartId) {
        try {
            return ResponseEntity.ok(cartService.listCartItemDTOs(cartId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/details/{detailId}")
    public ResponseEntity<CartItemDTO> updateCartDetail(@PathVariable Long detailId, @RequestParam int newQuantity) {
        try {
            CartItemDTO updatedCartDetail = cartService.updateCartItemDTO(detailId, newQuantity);
            return ResponseEntity.ok(updatedCartDetail);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/details/{detailId}")
    public ResponseEntity<?> deleteCartDetail(@PathVariable Long detailId) {
        cartService.deleteCartDetail(detailId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cartId}/clear")
    public ResponseEntity<?> clearCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.ok().build();
    }
}
