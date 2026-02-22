package com.musicshop.image;

import com.musicshop.model.product.Product;
import com.musicshop.model.product.ProductImage;
import com.musicshop.repository.product.ProductImageRepository;
import com.musicshop.repository.product.ProductRepository;
import com.musicshop.infrastructure.image.ImageUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageUploadServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    @InjectMocks
    private ImageUploadService imageUploadService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(imageUploadService, "uploadDir", tempDir.toString());
    }

    @Test
    void deletePrimaryImagePromotesNextImageAndNormalizesOrder() throws Exception {
        Long productId = 145L;
        Long deletedImageId = 100L;

        Product product = new Product();
        product.setId(productId);

        ProductImage deletedPrimary = new ProductImage();
        deletedPrimary.setId(deletedImageId);
        deletedPrimary.setProduct(product);
        deletedPrimary.setPrimary(true);
        deletedPrimary.setDisplayOrder(0);
        deletedPrimary.setUrl("/uploads/primary.png");

        ProductImage nextImage = new ProductImage();
        nextImage.setId(101L);
        nextImage.setProduct(product);
        nextImage.setPrimary(false);
        nextImage.setDisplayOrder(1);
        nextImage.setUrl("/uploads/next.png");

        ProductImage thirdImage = new ProductImage();
        thirdImage.setId(102L);
        thirdImage.setProduct(product);
        thirdImage.setPrimary(false);
        thirdImage.setDisplayOrder(2);
        thirdImage.setUrl("/uploads/third.png");

        Files.createFile(tempDir.resolve("primary.png"));

        when(productImageRepository.findById(deletedImageId)).thenReturn(Optional.of(deletedPrimary));
        when(productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId))
                .thenReturn(List.of(nextImage, thirdImage));

        imageUploadService.deleteProductImage(deletedImageId);

        verify(productImageRepository).delete(deletedPrimary);

        ArgumentCaptor<ProductImage> saveCaptor = ArgumentCaptor.forClass(ProductImage.class);
        verify(productImageRepository, org.mockito.Mockito.times(2)).save(saveCaptor.capture());
        List<ProductImage> saved = saveCaptor.getAllValues();

        ProductImage savedFirst = saved.stream().filter(img -> img.getId().equals(101L)).findFirst().orElseThrow();
        ProductImage savedSecond = saved.stream().filter(img -> img.getId().equals(102L)).findFirst().orElseThrow();

        assertTrue(savedFirst.isPrimary());
        assertTrue(savedFirst.getDisplayOrder() == 0);
        assertTrue(!savedSecond.isPrimary());
        assertTrue(savedSecond.getDisplayOrder() == 1);
    }
}
