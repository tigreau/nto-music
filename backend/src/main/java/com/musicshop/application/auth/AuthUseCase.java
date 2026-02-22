package com.musicshop.application.auth;

import com.musicshop.dto.auth.AuthResponse;
import com.musicshop.dto.auth.LoginRequest;
import com.musicshop.dto.auth.RegisterRequest;
import com.musicshop.service.auth.AuthService;
import org.springframework.stereotype.Component;

@Component
public class AuthUseCase {

    private final AuthService authService;

    public AuthUseCase(AuthService authService) {
        this.authService = authService;
    }

    public AuthResponse register(RegisterRequest request) {
        return authService.register(request).getResponse();
    }

    public AuthResponse login(LoginRequest request) {
        return authService.login(request).getResponse();
    }

    public AuthResponse getAuthenticatedUser(String email) {
        return authService.getAuthenticatedUser(email);
    }

    public void logout() {
        // The current logout behavior is stateless (cookie invalidation only).
    }
}
