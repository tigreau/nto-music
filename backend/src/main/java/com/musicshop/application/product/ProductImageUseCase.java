package com.musicshop.application.product;

import com.musicshop.dto.product.ProductImageDTO;
import com.musicshop.model.product.ProductImage;
import com.musicshop.service.image.ImageUploadService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductImageUseCase {

    private final ImageUploadService imageUploadService;

    public ProductImageUseCase(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ProductImageDTO uploadImage(Long productId, MultipartFile file, String altText, boolean isPrimary) {
        ProductImage image = imageUploadService.uploadProductImage(productId, file, altText, isPrimary);
        return new ProductImageDTO(
                image.getId(),
                image.getUrl(),
                image.getAltText(),
                image.isPrimary(),
                image.getDisplayOrder()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteImage(Long imageId) {
        imageUploadService.deleteProductImage(imageId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void setPrimaryImage(Long productId, Long imageId) {
        imageUploadService.setPrimaryImage(productId, imageId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void reorderImages(Long productId, List<Long> imageIds) {
        imageUploadService.reorderImages(productId, imageIds);
    }
}
