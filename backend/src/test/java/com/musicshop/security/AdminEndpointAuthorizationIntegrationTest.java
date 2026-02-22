package com.musicshop.security;

import com.musicshop.application.category.CategoryUseCase;
import com.musicshop.application.product.ProductUseCase;
import com.musicshop.application.product.ProductImageUseCase;
import com.musicshop.controller.category.CategoryController;
import com.musicshop.controller.product.ProductController;
import com.musicshop.controller.product.ProductImageController;
import com.musicshop.dto.category.CategoryDTO;
import com.musicshop.dto.product.DetailedProductDTO;
import com.musicshop.dto.product.ImageUploadCommand;
import com.musicshop.dto.product.ProductImageDTO;
import com.musicshop.exception.GlobalExceptionHandler;
import com.musicshop.mapper.ProductImageUploadMapper;
import com.musicshop.model.product.ProductCondition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        ProductController.class,
        ProductImageController.class,
        CategoryController.class
})
@AutoConfigureMockMvc(addFilters = false)
@Import({ AdminEndpointAuthorizationIntegrationTest.TestSecurityConfig.class, GlobalExceptionHandler.class })
class AdminEndpointAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private ProductUseCase productUseCase;

    @MockBean
    private ProductImageUseCase productImageUseCase;

    @MockBean
    private ProductImageUploadMapper productImageUploadMapper;

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
    @WithMockUser(username = "buyer@example.com", roles = "USER")
    void productCreate_deniesUser() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType("application/json")
                        .content(validProductCreatePayload()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void productCreate_allowsAdmin() throws Exception {
        when(productUseCase.createProduct(any())).thenReturn(new DetailedProductDTO(
                1L,
                "Guitar",
                "guitar",
                "desc",
                BigDecimal.valueOf(1000),
                2,
                "Guitars",
                "Fender",
                ProductCondition.EXCELLENT,
                null,
                false,
                null));

        mockMvc.perform(post("/api/products")
                        .contentType("application/json")
                        .content(validProductCreatePayload()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "buyer@example.com", roles = "USER")
    void categoryCreate_deniesUser() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType("application/json")
                        .content("{\"categoryName\":\"Pedals\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void categoryCreate_allowsAdmin() throws Exception {
        CategoryDTO categoryDTO = new CategoryDTO(1L, "Pedals", "pedals", null, 0L, null);
        when(categoryUseCase.createCategory(any(), eq(null))).thenReturn(categoryDTO);

        mockMvc.perform(post("/api/categories")
                        .contentType("application/json")
                        .content("{\"categoryName\":\"Pedals\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "buyer@example.com", roles = "USER")
    void imageUpload_deniesUser() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "img.jpg", "image/jpeg", "x".getBytes());

        mockMvc.perform(multipart("/api/products/10/images").file(file))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void imageUpload_allowsAdmin() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "img.jpg", "image/jpeg", "x".getBytes());
        when(productImageUploadMapper.toCommand(any(), anyString(), anyBoolean()))
                .thenReturn(new ImageUploadCommand(new byte[] { 1 }, "img.jpg", "image/jpeg", 1L, null, false));
        when(productImageUseCase.uploadImage(eq(10L), any(ImageUploadCommand.class)))
                .thenReturn(new ProductImageDTO(1L, "url", null, true, 0));

        mockMvc.perform(multipart("/api/products/10/images").file(file))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "buyer@example.com", roles = "USER")
    void productDelete_deniesUser() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void productDelete_allowsAdmin() throws Exception {
        doNothing().when(productUseCase).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    private static String validProductCreatePayload() {
        return """
                {
                  "name": "Guitar",
                  "description": "desc",
                  "price": 1000.00,
                  "quantityAvailable": 2,
                  "categoryId": 1,
                  "condition": "EXCELLENT"
                }
                """;
    }
}
