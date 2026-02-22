package com.musicshop.application.order;

import com.musicshop.dto.checkout.CheckoutRequest;
import com.musicshop.dto.checkout.CheckoutResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import com.musicshop.service.checkout.CheckoutFacade;
import com.musicshop.service.user.UserService;
import org.springframework.stereotype.Service;

@Service
public class CheckoutUseCase {

    private final CheckoutFacade checkoutFacade;
    private final UserService userService;

    public CheckoutUseCase(CheckoutFacade checkoutFacade, UserService userService) {
        this.checkoutFacade = checkoutFacade;
        this.userService = userService;
    }

    @PreAuthorize("isAuthenticated()")
    public CheckoutResponse checkout(String email, CheckoutRequest request) {
        Long userId = userService.findUserIdByEmail(email);
        return checkoutFacade.checkout(userId, request);
    }
}
