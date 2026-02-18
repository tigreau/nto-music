package com.musicshop.controller.order;

import com.musicshop.application.order.CheckoutUseCase;
import com.musicshop.dto.checkout.CheckoutRequest;
import com.musicshop.dto.checkout.CheckoutResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CheckoutUseCase checkoutUseCase;

    @Autowired
    public OrderController(CheckoutUseCase checkoutUseCase) {
        this.checkoutUseCase = checkoutUseCase;
    }

    @PostMapping("/checkout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CheckoutResponse> checkout(@Valid @RequestBody CheckoutRequest request,
                                                     Authentication authentication) {
        CheckoutResponse response = checkoutUseCase.checkout(authentication.getName(), request);
        return ResponseEntity.ok(response);
    }
}
