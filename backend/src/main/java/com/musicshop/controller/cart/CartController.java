package com.musicshop.controller.cart;

import com.musicshop.application.cart.CartUseCase;
import com.musicshop.dto.cart.CartItemDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/api/carts")
@Validated
public class CartController {

    private final CartUseCase cartUseCase;

    @Autowired
    public CartController(CartUseCase cartUseCase) {
        this.cartUseCase = cartUseCase;
    }

    @PostMapping("/my/products/{productId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Add product to current user's cart")
    @ApiResponse(responseCode = "204", description = "No Content")
    public ResponseEntity<Void> addProductToMyCart(@PathVariable Long productId,
            @RequestParam @Min(1) int quantity,
            Authentication authentication) {
        cartUseCase.addProduct(authentication.getName(), productId, quantity);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my/details")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CartItemDTO>> listMyCartDetails(Authentication authentication) {
        return ResponseEntity.ok(cartUseCase.listCartDetails(authentication.getName()));
    }

    @DeleteMapping("/my/clear")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Clear current user's cart")
    @ApiResponse(responseCode = "204", description = "No Content")
    public ResponseEntity<Void> clearMyCart(Authentication authentication) {
        cartUseCase.clearCart(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/details/{detailId}")
    @PreAuthorize("@accessGuard.canAccessCartDetail(#detailId, authentication)")
    public ResponseEntity<CartItemDTO> updateCartDetail(@PathVariable Long detailId, @RequestParam @Min(1) int newQuantity) {
        CartItemDTO updatedCartDetail = cartUseCase.updateCartDetail(detailId, newQuantity);
        return ResponseEntity.ok(updatedCartDetail);
    }

    @DeleteMapping("/details/{detailId}")
    @PreAuthorize("@accessGuard.canAccessCartDetail(#detailId, authentication)")
    @Operation(summary = "Delete one cart item detail")
    @ApiResponse(responseCode = "204", description = "No Content")
    public ResponseEntity<Void> deleteCartDetail(@PathVariable Long detailId) {
        cartUseCase.deleteCartDetail(detailId);
        return ResponseEntity.noContent().build();
    }
}
