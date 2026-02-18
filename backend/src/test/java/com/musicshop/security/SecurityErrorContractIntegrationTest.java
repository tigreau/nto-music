package com.musicshop.security;

import com.musicshop.application.category.CategoryUseCase;
import com.musicshop.config.SecurityConfig;
import com.musicshop.controller.cart.CartController;
import com.musicshop.controller.category.CategoryController;
import com.musicshop.application.cart.CartUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = { CategoryController.class, CartController.class })
@AutoConfigureMockMvc
@Import({
        SecurityConfig.class,
        RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class
})
class SecurityErrorContractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryUseCase categoryUseCase;

    @MockBean
    private CartUseCase cartUseCase;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void unauthenticatedCartEndpoint_returnsCanonicalUnauthorized() throws Exception {
        mockMvc.perform(get("/api/carts/my/details"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void unauthenticatedAdminEndpoint_returnsCanonicalUnauthorized() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType("application/json")
                        .content("{\"categoryName\":\"Pedals\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @WithMockUser(username = "buyer@example.com", roles = "USER")
    void nonAdminUserOnAdminEndpoint_returnsCanonicalForbidden() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType("application/json")
                        .content("{\"categoryName\":\"Pedals\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.status").value(403));
    }
}
