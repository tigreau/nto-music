package com.musicshop.application.order;

import com.musicshop.dto.checkout.CheckoutRequest;
import com.musicshop.dto.checkout.CheckoutResponse;
import com.musicshop.model.user.User;
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
        User user = userService.findByEmail(email);
        return checkoutFacade.checkout(user, request);
    }
}
