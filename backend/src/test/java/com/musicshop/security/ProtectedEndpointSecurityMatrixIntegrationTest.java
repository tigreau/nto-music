package com.musicshop.security;

import com.musicshop.application.cart.CartUseCase;
import com.musicshop.application.category.CategoryUseCase;
import com.musicshop.application.notification.NotificationUseCase;
import com.musicshop.application.order.CheckoutUseCase;
import com.musicshop.application.product.ProductImageUseCase;
import com.musicshop.application.product.ProductUseCase;
import com.musicshop.application.user.UserUseCase;
import com.musicshop.config.SecurityConfig;
import com.musicshop.controller.cart.CartController;
import com.musicshop.controller.category.CategoryController;
import com.musicshop.controller.order.OrderController;
import com.musicshop.controller.product.ProductController;
import com.musicshop.controller.product.ProductImageController;
import com.musicshop.controller.user.NotificationController;
import com.musicshop.controller.user.UserController;
import com.musicshop.dto.cart.CartItemDTO;
import com.musicshop.model.cart.Cart;
import com.musicshop.model.cart.CartDetail;
import com.musicshop.model.user.Notification;
import com.musicshop.model.user.User;
import com.musicshop.repository.cart.CartDetailRepository;
import com.musicshop.repository.user.NotificationRepository;
import com.musicshop.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        UserController.class,
        CartController.class,
        NotificationController.class,
        ProductController.class,
        ProductImageController.class,
        CategoryController.class,
        OrderController.class
})
@AutoConfigureMockMvc
@Import({
        SecurityConfig.class,
        RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class,
        AccessGuard.class
})
class ProtectedEndpointSecurityMatrixIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductUseCase productUseCase;

    @MockBean
    private ProductImageUseCase productImageUseCase;

    @MockBean
    private CategoryUseCase categoryUseCase;

    @MockBean
    private CheckoutUseCase checkoutUseCase;

    @MockBean
    private UserUseCase userUseCase;

    @MockBean
    private CartUseCase cartUseCase;

    @MockBean
    private NotificationUseCase notificationUseCase;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CartDetailRepository cartDetailRepository;

    @MockBean
    private NotificationRepository notificationRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void unauthenticatedProtectedEndpoints_returnCanonicalUnauthorized() throws Exception {
        mockMvc.perform(get("/api/carts/my/details"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));

        mockMvc.perform(post("/api/products")
                        .contentType("application/json")
                        .content(validProductPayload()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(username = "buyer@example.com", roles = "USER")
    void userRole_adminEndpoints_returnCanonicalForbidden() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType("application/json")
                        .content("{\"categoryName\":\"Pedals\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

        mockMvc.perform(post("/api/products")
                        .contentType("application/json")
                        .content(validProductPayload()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

        MockMultipartFile file = new MockMultipartFile("file", "img.jpg", "image/jpeg", "x".getBytes());
        mockMvc.perform(multipart("/api/products/10/images").file(file))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "other@example.com", roles = "USER")
    void ownershipGuardEndpoints_otherUser_returnCanonicalForbidden() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithEmail("self@example.com")));
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

        when(cartDetailRepository.findById(10L)).thenReturn(Optional.of(cartDetailFor("self@example.com")));
        mockMvc.perform(delete("/api/carts/details/10"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

        when(notificationRepository.findById(15L)).thenReturn(Optional.of(notificationFor("self@example.com")));
        mockMvc.perform(delete("/api/notifications/15"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "self@example.com", roles = "USER")
    void ownershipGuardEndpoints_owner_returnOk() throws Exception {
        when(cartDetailRepository.findById(10L)).thenReturn(Optional.of(cartDetailFor("self@example.com")));
        CartItemDTO updated = new CartItemDTO();
        updated.setId(10L);
        when(cartUseCase.updateCartDetail(10L, 2)).thenReturn(updated);
        mockMvc.perform(put("/api/carts/details/10").param("newQuantity", "2"))
                .andExpect(status().isOk());

        when(notificationRepository.findById(15L)).thenReturn(Optional.of(notificationFor("self@example.com")));
        mockMvc.perform(patch("/api/notifications/15/read"))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/notifications/15"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void ownershipGuardEndpoints_admin_returnOk() throws Exception {
        CartItemDTO updated = new CartItemDTO();
        updated.setId(10L);
        when(cartUseCase.updateCartDetail(10L, 2)).thenReturn(updated);
        mockMvc.perform(put("/api/carts/details/10").param("newQuantity", "2"))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/notifications/15/read"))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/notifications/15"))
                .andExpect(status().isNoContent());
    }

    private static String validProductPayload() {
        return """
                {
                  "name": "Guitar",
                  "description": "desc",
                  "price": 1000.00,
                  "quantityAvailable": 2,
                  "categoryId": 1,
                  "condition": "NEW"
                }
                """;
    }

    private static User userWithEmail(String email) {
        User user = new User();
        user.setEmail(email);
        return user;
    }

    private static CartDetail cartDetailFor(String email) {
        Cart cart = new Cart();
        cart.setUser(userWithEmail(email));

        CartDetail detail = new CartDetail();
        detail.setCart(cart);
        return detail;
    }

    private static Notification notificationFor(String email) {
        Notification notification = new Notification();
        notification.setUser(userWithEmail(email));
        return notification;
    }
}
