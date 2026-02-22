package com.musicshop.mapper;

import com.musicshop.dto.auth.AuthResponse;
import com.musicshop.model.user.User;
import com.musicshop.model.user.UserRole;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuthMapperTest {

    private final AuthMapper authMapper = Mappers.getMapper(AuthMapper.class);

    @Test
    void toAuthResponse_mapsUserIdAndRole() {
        User user = new User();
        user.setId(7L);
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(UserRole.CUSTOMER);

        AuthResponse response = authMapper.toAuthResponse(user, null);

        assertEquals(7L, response.getUserId());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("CUSTOMER", response.getRole());
        assertNull(response.getToken());
    }
}
