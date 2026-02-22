//package com.musicshop.product;
//
//import com.musicshop.domain.discount.DiscountStrategyFactory;
//import com.musicshop.event.product.ProductDeletionEvent;
//import com.musicshop.event.product.ProductDiscountEvent;
//import com.musicshop.event.product.ProductUpdateEvent;
//import com.musicshop.model.category.Category;
//import com.musicshop.model.product.Product;
//import com.musicshop.repository.cart.CartDetailRepository;
//import com.musicshop.repository.category.CategoryRepository;
//import com.musicshop.repository.product.ProductRepository;
//import com.musicshop.service.product.ProductService;
//import com.musicshop.validation.product.ProductValidator;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.context.ApplicationEventPublisher;
//
//import javax.xml.bind.ValidationException;
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class ProductServiceTest {
//
//    @Mock
//    private ProductRepository productRepository;
//    @Mock
//    private CategoryRepository categoryRepository;
//    @Mock
//    private ApplicationEventPublisher eventPublisher;
//    @Mock
//    private DiscountStrategyFactory discountStrategyFactory;
//    @Mock
//    private CartDetailRepository cartDetailRepository;
//    @Mock
//    private ProductValidator productValidator;
//
//    @InjectMocks
//    private ProductService productService;
//
//    private Product product;
//
//    @BeforeEach
//    void setUp() {
//        product = new Product();
//        product.setId(1L);
//        product.setName("Guitar");
//        product.setPrice(new BigDecimal("500"));
//        product.setDescription("An acoustic guitar");
//        product.setQuantityAvailable(10);
//        product.setCategory(new Category());
//    }
//
//    @Test
//    void createProduct_Success() throws ValidationException {
//        when(productRepository.save(any(Product.class))).thenReturn(product);
//        Product createdProduct = productService.createProduct(product);
//        assertNotNull(createdProduct);
//        assertEquals("Guitar", createdProduct.getName());
//        verify(productValidator).validate(product);
//    }
//
//    @Test
//    void getProductById_Found() {
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        Optional<Product> foundProduct = productService.getProductById(1L);
//        assertTrue(foundProduct.isPresent());
//        assertEquals("Guitar", foundProduct.get().getName());
//    }
//
//    @Test
//    void getProductById_NotFound() {
//        when(productRepository.findById(1L)).thenReturn(Optional.empty());
//        Optional<Product> foundProduct = productService.getProductById(1L);
//        assertFalse(foundProduct.isPresent());
//    }
//
//    @Test
//    void updateProduct_Success() {
//        Product updatedDetails = new Product();
//        updatedDetails.setPrice(new BigDecimal("600"));
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(productRepository.save(any(Product.class))).thenReturn(product);
//        Optional<Product> updatedProduct = productService.updateProduct(1L, updatedDetails);
//        assertTrue(updatedProduct.isPresent());
//        assertEquals(new BigDecimal("600"), updatedProduct.get().getPrice());
//        verify(eventPublisher).publishEvent(any(ProductUpdateEvent.class));
//    }
//
//    @Test
//    void deleteProduct_Success() {
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        productService.deleteProduct(1L);
//        verify(productRepository).delete(product);
//        verify(eventPublisher).publishEvent(any(ProductDeletionEvent.class));
//    }
//
//    @Test
//    void applyDiscount_Success() {
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(discountStrategyFactory.getDiscountStrategy("fixed")).thenReturn(amount -> new BigDecimal("400"));
//        Optional<Product> discountedProduct = productService.applyDiscount(1L, "fixed");
//        assertTrue(discountedProduct.isPresent());
//        assertEquals(new BigDecimal("400"), discountedProduct.get().getPrice());
//        verify(eventPublisher).publishEvent(any(ProductDiscountEvent.class));
//    }
//}
