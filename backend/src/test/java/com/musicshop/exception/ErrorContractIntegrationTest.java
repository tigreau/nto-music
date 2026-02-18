package com.musicshop.exception;

import com.musicshop.application.category.CategoryUseCase;
import com.musicshop.application.product.ProductUseCase;
import com.musicshop.application.user.UserUseCase;
import com.musicshop.controller.category.CategoryController;
import com.musicshop.controller.product.ProductController;
import com.musicshop.controller.user.UserController;
import com.musicshop.dto.category.CreateCategoryRequest;
import com.musicshop.security.AccessGuard;
import com.musicshop.security.JwtAuthenticationFilter;
import com.musicshop.model.user.User;
import com.musicshop.repository.cart.CartDetailRepository;
import com.musicshop.repository.user.NotificationRepository;
import com.musicshop.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = { UserController.class, CategoryController.class, ProductController.class })
@AutoConfigureMockMvc(addFilters = false)
@Import({ ErrorContractIntegrationTest.TestSecurityConfig.class, GlobalExceptionHandler.class, AccessGuard.class })
class ErrorContractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserUseCase userUseCase;

    @MockBean
    private CategoryUseCase categoryUseCase;

    @MockBean
    private ProductUseCase productUseCase;

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
    void resourceNotFound_isCanonical() throws Exception {
        User owner = new User();
        owner.setEmail("self@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(userUseCase.getUser(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    @WithMockUser(username = "other@example.com", roles = "USER")
    void accessDenied_isCanonical() throws Exception {
        User owner = new User();
        owner.setEmail("self@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.message").value("Access is denied"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void invalidArgument_isCanonical() throws Exception {
        when(categoryUseCase.createCategory(any(CreateCategoryRequest.class), eq(null)))
                .thenThrow(new IllegalArgumentException("Slug already exists"));

        mockMvc.perform(post("/api/categories")
                        .contentType("application/json")
                        .content("{\"categoryName\":\"Pedals\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_ARGUMENT"))
                .andExpect(jsonPath("$.message", containsString("Slug already exists")));
    }

    @Test
    void productNotFound_isCanonical() throws Exception {
        when(productUseCase.getDetailedProductById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void productUpdateNotFound_isCanonical() throws Exception {
        when(productUseCase.updateProduct(eq(999L), any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/products/999")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "Fender Stratocaster",
                                  "description": "Used electric guitar",
                                  "price": 1200.00,
                                  "quantityAvailable": 2,
                                  "categoryId": 1,
                                  "condition": "GOOD"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void productDeleteNotFound_isCanonical() throws Exception {
        when(productUseCase.existsById(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void applyDiscountInvalidArgument_isCanonical() throws Exception {
        when(productUseCase.applyDiscount(1L, "UNKNOWN"))
                .thenThrow(new IllegalArgumentException("Unknown discount type: UNKNOWN"));

        mockMvc.perform(patch("/api/products/1/apply-discount")
                        .param("discountType", "UNKNOWN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_ARGUMENT"))
                .andExpect(jsonPath("$.message", containsString("Unknown discount type")));
    }
}
