package com.musicshop.dto.auth;

public record AuthResponse(
        String token,
        Long userId,
        String email,
        String firstName,
        String lastName,
        String role) {

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRole() {
        return role;
    }
}
