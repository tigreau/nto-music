package com.musicshop.security;

import com.musicshop.application.user.UserUseCase;
import com.musicshop.controller.cart.CartController;
import com.musicshop.controller.user.NotificationController;
import com.musicshop.controller.user.UserController;
import com.musicshop.dto.cart.CartItemDTO;
import com.musicshop.dto.user.UserDTO;
import com.musicshop.exception.GlobalExceptionHandler;
import com.musicshop.infrastructure.notification.NotificationSseBroker;
import com.musicshop.model.cart.Cart;
import com.musicshop.model.cart.CartDetail;
import com.musicshop.model.user.Notification;
import com.musicshop.model.user.User;
import com.musicshop.repository.cart.CartDetailRepository;
import com.musicshop.repository.user.NotificationRepository;
import com.musicshop.repository.user.UserRepository;
import com.musicshop.application.cart.CartUseCase;
import com.musicshop.application.notification.NotificationUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        UserController.class,
        CartController.class,
        NotificationController.class
})
@AutoConfigureMockMvc(addFilters = false)
@Import({ EndpointAuthorizationIntegrationTest.TestSecurityConfig.class, GlobalExceptionHandler.class, AccessGuard.class })
class EndpointAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserUseCase userUseCase;

    @MockBean
    private CartUseCase cartUseCase;

    @MockBean
    private NotificationUseCase notificationUseCase;

    @MockBean
    private NotificationSseBroker notificationSseBroker;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CartDetailRepository cartDetailRepository;

    @MockBean
    private NotificationRepository notificationRepository;

    @TestConfiguration
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf().disable().authorizeRequests().anyRequest().permitAll();
            return http.build();
        }
    }

    @Test
    @WithMockUser(username = "self@example.com", roles = "USER")
    void userEndpoint_allowsSelf() throws Exception {
        User owner = userWithEmail("self@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(userUseCase.getUser(1L)).thenReturn(Optional.of(
                new UserDTO(1L, "Self", "User", "self@example.com", "123",
                        "Main Street", "42A", "10001", "Amsterdam", "Netherlands")));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "other@example.com", roles = "USER")
    void userEndpoint_deniesDifferentUser() throws Exception {
        User owner = userWithEmail("self@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void userEndpoint_allowsAdmin() throws Exception {
        when(userUseCase.getUser(1L)).thenReturn(Optional.of(
                new UserDTO(1L, "Admin", "User", "admin@example.com", "123",
                        "Admin Avenue", "1", "10100", "Rotterdam", "Netherlands")));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "self@example.com", roles = "USER")
    void cartDetailEndpoint_allowsOwner() throws Exception {
        when(cartDetailRepository.findById(10L)).thenReturn(Optional.of(cartDetailFor("self@example.com")));
        CartItemDTO cartItemDTO = new CartItemDTO(10L, null, 2, null);
        when(cartUseCase.updateCartDetail(10L, 2)).thenReturn(cartItemDTO);

        mockMvc.perform(put("/api/carts/details/10").param("newQuantity", "2"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "other@example.com", roles = "USER")
    void cartDetailEndpoint_deniesDifferentUser() throws Exception {
        when(cartDetailRepository.findById(10L)).thenReturn(Optional.of(cartDetailFor("self@example.com")));

        mockMvc.perform(put("/api/carts/details/10").param("newQuantity", "2"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void cartDetailEndpoint_allowsAdmin() throws Exception {
        CartItemDTO cartItemDTO = new CartItemDTO(10L, null, 2, null);
        when(cartUseCase.updateCartDetail(10L, 2)).thenReturn(cartItemDTO);

        mockMvc.perform(put("/api/carts/details/10").param("newQuantity", "2"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "self@example.com", roles = "USER")
    void notificationEndpoint_allowsOwner() throws Exception {
        when(notificationRepository.findById(15L)).thenReturn(Optional.of(notificationFor("self@example.com")));
        doNothing().when(notificationUseCase).deleteNotification(15L);

        mockMvc.perform(delete("/api/notifications/15"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "other@example.com", roles = "USER")
    void notificationEndpoint_deniesDifferentUser() throws Exception {
        when(notificationRepository.findById(15L)).thenReturn(Optional.of(notificationFor("self@example.com")));

        mockMvc.perform(delete("/api/notifications/15"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void notificationEndpoint_allowsAdmin() throws Exception {
        doNothing().when(notificationUseCase).deleteNotification(15L);

        mockMvc.perform(delete("/api/notifications/15"))
                .andExpect(status().isNoContent());
    }

    private static User userWithEmail(String email) {
        User user = new User();
        user.setEmail(email);
        return user;
    }

    private static CartDetail cartDetailFor(String userEmail) {
        User user = userWithEmail(userEmail);
        Cart cart = new Cart();
        cart.setUser(user);

        CartDetail detail = new CartDetail();
        detail.setCart(cart);
        return detail;
    }

    private static Notification notificationFor(String userEmail) {
        Notification notification = new Notification();
        notification.setUser(userWithEmail(userEmail));
        return notification;
    }
}
