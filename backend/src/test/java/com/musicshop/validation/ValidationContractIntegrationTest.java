package com.musicshop.validation;

import com.musicshop.application.cart.CartUseCase;
import com.musicshop.application.category.CategoryUseCase;
import com.musicshop.application.order.CheckoutUseCase;
import com.musicshop.application.product.ProductUseCase;
import com.musicshop.controller.cart.CartController;
import com.musicshop.controller.category.CategoryController;
import com.musicshop.controller.order.OrderController;
import com.musicshop.controller.product.ProductController;
import com.musicshop.exception.GlobalExceptionHandler;
import com.musicshop.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        ProductController.class,
        OrderController.class,
        CartController.class,
        CategoryController.class
})
@AutoConfigureMockMvc(addFilters = false)
@Import({ ValidationContractIntegrationTest.TestSecurityConfig.class, GlobalExceptionHandler.class })
class ValidationContractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private ProductUseCase productUseCase;

    @MockBean
    private CheckoutUseCase checkoutUseCase;

    @MockBean
    private CartUseCase cartUseCase;

    @MockBean
    private CategoryUseCase categoryUseCase;

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
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void invalidProductCreate_returnsCanonicalValidationError() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message", containsString("name")));
    }

    @Test
    @WithMockUser(username = "buyer@example.com", roles = "USER")
    void invalidCheckout_returnsCanonicalValidationError() throws Exception {
        String invalidCheckout = """
                {
                  "paymentMethod": "",
                  "street": "",
                  "number": "",
                  "postalCode": "",
                  "city": "",
                  "country": ""
                }
                """;

        mockMvc.perform(post("/api/orders/checkout")
                        .contentType("application/json")
                        .content(invalidCheckout))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message", containsString("paymentMethod")));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void invalidCategoryCreate_returnsCanonicalValidationError() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .param("parentId", "1")
                        .contentType("application/json")
                        .content("{\"categoryName\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message", containsString("categoryName")));
    }

    @Test
    @WithMockUser(username = "buyer@example.com", roles = "USER")
    void invalidCartAddQuantity_returnsCanonicalValidationError() throws Exception {
        mockMvc.perform(post("/api/carts/my/products/10")
                        .param("quantity", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message", containsString("quantity")));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void blankDiscountType_returnsCanonicalValidationError() throws Exception {
        mockMvc.perform(patch("/api/products/1/apply-discount")
                        .param("discountType", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message", containsString("discountType")));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void invalidProductEnumCoercion_returnsCanonicalValidationError() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "Guitar",
                                  "description": "desc",
                                  "price": 1000.00,
                                  "quantityAvailable": 2,
                                  "categoryId": 1,
                                  "condition": "BROKEN"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message", containsString("condition")));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void invalidProductNumberCoercion_returnsCanonicalValidationError() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "Guitar",
                                  "description": "desc",
                                  "price": "not-a-number",
                                  "quantityAvailable": 2,
                                  "categoryId": 1,
                                  "condition": "EXCELLENT"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message", containsString("price")));
    }

    @Test
    void invalidProductIdPathType_returnsCanonicalValidationError() throws Exception {
        mockMvc.perform(get("/api/products/not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message", containsString("id")));
    }

    @Test
    @WithMockUser(username = "buyer@example.com", roles = "USER")
    void invalidCartQuantityParamType_returnsCanonicalValidationError() throws Exception {
        mockMvc.perform(post("/api/carts/my/products/10")
                        .param("quantity", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message", containsString("quantity")));
    }
}
