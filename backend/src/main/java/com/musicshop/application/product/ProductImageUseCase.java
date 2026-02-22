package com.musicshop.application.product;

import com.musicshop.dto.product.ProductImageDTO;
import com.musicshop.dto.product.ImageUploadCommand;
import com.musicshop.service.image.ProductImageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImageUseCase {

    private final ProductImageService productImageService;

    public ProductImageUseCase(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ProductImageDTO uploadImage(Long productId, ImageUploadCommand command) {
        return productImageService.uploadProductImage(productId, command);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteImage(Long imageId) {
        productImageService.deleteProductImage(imageId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void setPrimaryImage(Long productId, Long imageId) {
        productImageService.setPrimaryImage(productId, imageId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void reorderImages(Long productId, List<Long> imageIds) {
        productImageService.reorderImages(productId, imageIds);
    }
}
