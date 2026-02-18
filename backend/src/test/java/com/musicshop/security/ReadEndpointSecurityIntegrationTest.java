package com.musicshop.security;

import com.musicshop.application.auth.AuthUseCase;
import com.musicshop.application.notification.NotificationUseCase;
import com.musicshop.config.SecurityConfig;
import com.musicshop.controller.auth.AuthController;
import com.musicshop.controller.user.NotificationController;
import com.musicshop.dto.auth.AuthResponse;
import com.musicshop.dto.user.NotificationDTO;
import com.musicshop.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = { AuthController.class, NotificationController.class })
@AutoConfigureMockMvc
@Import({
        SecurityConfig.class,
        RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class,
        GlobalExceptionHandler.class
})
@TestPropertySource(properties = "jwt.expiration=3600000")
class ReadEndpointSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthUseCase authUseCase;

    @MockBean
    private NotificationUseCase notificationUseCase;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void unauthenticatedNotificationsEndpoints_returnCanonicalUnauthorized() throws Exception {
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));

        mockMvc.perform(get("/api/notifications/stream"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void unauthenticatedAuthMe_returnsCanonicalUnauthorized() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(username = "buyer@example.com", roles = "USER")
    void authenticatedAuthMe_returnsUserProfile() throws Exception {
        when(authUseCase.getAuthenticatedUser("buyer@example.com"))
                .thenReturn(new AuthResponse(null, 1L, "buyer@example.com", "Buyer", "User", "USER"));

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("buyer@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @WithMockUser(username = "buyer@example.com", roles = "USER")
    void authenticatedNotificationsEndpoints_returnOk() throws Exception {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(11L);
        dto.setMessage("Order updated");
        dto.setType("ORDER_STATUS");
        dto.setTimestamp("2026-02-17T10:00:00Z");
        dto.setRead(false);
        when(notificationUseCase.getNotifications("buyer@example.com")).thenReturn(List.of(dto));
        when(notificationUseCase.stream("buyer@example.com")).thenReturn(new SseEmitter());

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(11));

        mockMvc.perform(get("/api/notifications/stream"))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted());
    }
}
