package com.musicshop.controller.cart;

import com.musicshop.model.cart.CartDetail;
import com.musicshop.service.cart.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/carts")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{userId}/products/{productId}")
    public ResponseEntity<?> addProductToCart(@PathVariable Long userId, @PathVariable Long productId,
            @RequestParam int quantity) {
        try {
            CartDetail cartDetail = cartService.addProductToCart(userId, productId, quantity);
            return ResponseEntity.ok().body("Product added to cart");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{cartId}/products/{productId}")
    public ResponseEntity<CartDetail> getCartDetail(@PathVariable Long cartId, @PathVariable Long productId) {
        return cartService.getCartDetail(cartId, productId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{cartId}/details")
    public ResponseEntity<List<CartDetail>> listCartDetails(@PathVariable Long cartId) {
        try {
            List<CartDetail> cartDetails = cartService.listCartDetails(cartId);
            return ResponseEntity.ok(cartDetails);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/details/{detailId}")
    public ResponseEntity<CartDetail> updateCartDetail(@PathVariable Long detailId, @RequestParam int newQuantity) {
        try {
            CartDetail updatedCartDetail = cartService.updateCartDetail(detailId, newQuantity);
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
