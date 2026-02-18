package com.musicshop.exception;

import com.musicshop.dto.error.ApiErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        this.handler = new GlobalExceptionHandler();
    }

    @Test
    void handleNotFound_returnsCanonicalError() {
        ResponseEntity<ApiErrorResponse> response = handler.handleNotFound(
                new ResourceNotFoundException("Product not found"));

        assertError(response, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "Product not found");
    }

    @Test
    void handleBadCredentials_returnsCanonicalError() {
        ResponseEntity<ApiErrorResponse> response = handler.handleBadCredentials(
                new BadCredentialsException("Bad credentials"));

        assertError(response, HttpStatus.UNAUTHORIZED, "BAD_CREDENTIALS", "Bad credentials");
    }

    @Test
    void handleAuthenticationRequired_returnsCanonicalError() {
        ResponseEntity<ApiErrorResponse> response = handler.handleAuthenticationRequired(
                new AuthenticationCredentialsNotFoundException("Authentication required"));

        assertError(response, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Authentication required");
    }

    @Test
    void handleAccessDenied_returnsCanonicalError() {
        ResponseEntity<ApiErrorResponse> response = handler.handleAccessDenied(
                new AccessDeniedException("Denied"));

        assertError(response, HttpStatus.FORBIDDEN, "ACCESS_DENIED", "Access is denied");
    }

    @Test
    void handleGeneric_returnsCanonicalError() {
        ResponseEntity<ApiErrorResponse> response = handler.handleGeneric(
                new RuntimeException("Unexpected"));

        assertError(response, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred");
    }

    private void assertError(
            ResponseEntity<ApiErrorResponse> response,
            HttpStatus expectedStatus,
            String expectedCode,
            String expectedMessage) {
        assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
        ApiErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(expectedStatus.value());
        assertThat(body.getError()).isEqualTo(expectedStatus.getReasonPhrase());
        assertThat(body.getCode()).isEqualTo(expectedCode);
        assertThat(body.getMessage()).isEqualTo(expectedMessage);
        assertThat(body.getTimestamp()).isNotBlank();
    }
}
