package com.musicshop.exception;

import com.musicshop.application.auth.AuthUseCase;
import com.musicshop.application.cart.CartUseCase;
import com.musicshop.application.order.CheckoutUseCase;
import com.musicshop.controller.auth.AuthController;
import com.musicshop.controller.cart.CartController;
import com.musicshop.controller.order.OrderController;
import com.musicshop.dto.checkout.CheckoutRequest;
import com.musicshop.security.JwtAuthenticationFilter;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        OrderController.class,
        CartController.class,
        AuthController.class
})
@AutoConfigureMockMvc(addFilters = false)
@Import({ BusinessErrorContractIntegrationTest.TestSecurityConfig.class, GlobalExceptionHandler.class })
@TestPropertySource(properties = "jwt.expiration=3600000")
class BusinessErrorContractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CheckoutUseCase checkoutUseCase;

    @MockBean
    private CartUseCase cartUseCase;

    @MockBean
    private AuthUseCase authUseCase;

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
    @WithMockUser(username = "buyer@example.com", roles = "USER")
    void checkoutCartEmpty_isCanonical() throws Exception {
        doThrow(new CartEmptyException("Cart is empty"))
                .when(checkoutUseCase).checkout(eq("buyer@example.com"), any(CheckoutRequest.class));

        mockMvc.perform(post("/api/orders/checkout")
                        .principal(authPrincipal())
                        .contentType("application/json")
                        .content(validCheckoutPayload()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CART_EMPTY"))
                .andExpect(jsonPath("$.message").value("Cart is empty"));
    }

    @Test
    @WithMockUser(username = "buyer@example.com", roles = "USER")
    void checkoutPaymentFailed_isCanonical() throws Exception {
        doThrow(new PaymentFailedException("Payment failed: card declined"))
                .when(checkoutUseCase).checkout(eq("buyer@example.com"), any(CheckoutRequest.class));

        mockMvc.perform(post("/api/orders/checkout")
                        .principal(authPrincipal())
                        .contentType("application/json")
                        .content(validCheckoutPayload()))
                .andExpect(status().isPaymentRequired())
                .andExpect(jsonPath("$.code").value("PAYMENT_FAILED"))
                .andExpect(jsonPath("$.message", containsString("card declined")));
    }

    @Test
    @WithMockUser(username = "buyer@example.com", roles = "USER")
    void addToCartInsufficientStock_isCanonical() throws Exception {
        doThrow(new InsufficientStockException("Not enough quantity available"))
                .when(cartUseCase).addProduct("buyer@example.com", 10L, 99);

        mockMvc.perform(post("/api/carts/my/products/10")
                        .principal(authPrincipal())
                        .param("quantity", "99"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_STOCK"))
                .andExpect(jsonPath("$.message").value("Not enough quantity available"));
    }

    @Test
    void registerDuplicateEmail_isCanonical() throws Exception {
        doThrow(new DuplicateResourceException("Email already in use"))
                .when(authUseCase).register(any());

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content("""
                                {
                                  "firstName": "John",
                                  "lastName": "Doe",
                                  "email": "john@example.com",
                                  "password": "Password123!"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_RESOURCE"))
                .andExpect(jsonPath("$.message").value("Email already in use"));
    }

    private static String validCheckoutPayload() {
        return """
                {
                  "paymentMethod": "cod",
                  "street": "Main St",
                  "number": "12A",
                  "postalCode": "12345",
                  "city": "Nashville",
                  "country": "USA"
                }
                """;
    }

    private static UsernamePasswordAuthenticationToken authPrincipal() {
        return new UsernamePasswordAuthenticationToken(
                "buyer@example.com",
                "n/a",
                AuthorityUtils.createAuthorityList("ROLE_USER"));
    }
}
