package com.musicshop.controller.order;

import com.musicshop.dto.checkout.CheckoutRequest;
import com.musicshop.dto.checkout.CheckoutResponse;
import com.musicshop.model.user.User;
import com.musicshop.service.checkout.CheckoutFacade;
import com.musicshop.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CheckoutFacade checkoutFacade;
    private final UserService userService;

    @Autowired
    public OrderController(CheckoutFacade checkoutFacade, UserService userService) {
        this.checkoutFacade = checkoutFacade;
        this.userService = userService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(@RequestBody CheckoutRequest request,
                                                     Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        CheckoutResponse response = checkoutFacade.checkout(user, request);
        return ResponseEntity.ok(response);
    }
}
