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

    public AuthResult register(RegisterRequest request) {
        AuthService.AuthResult result = authService.register(request);
        return new AuthResult(result.getResponse(), result.getToken());
    }

    public AuthResult login(LoginRequest request) {
        AuthService.AuthResult result = authService.login(request);
        return new AuthResult(result.getResponse(), result.getToken());
    }

    public AuthResponse getAuthenticatedUser(String email) {
        return authService.getAuthenticatedUser(email);
    }

    public static class AuthResult {
        private final AuthResponse response;
        private final String token;

        public AuthResult(AuthResponse response, String token) {
            this.response = response;
            this.token = token;
        }

        public AuthResponse getResponse() {
            return response;
        }

        public String getToken() {
            return token;
        }
    }
}
