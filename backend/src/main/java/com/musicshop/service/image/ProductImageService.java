package com.musicshop.service.image;

import com.musicshop.dto.product.ImageUploadCommand;
import com.musicshop.dto.product.ProductImageDTO;
import com.musicshop.service.image.ImageUploader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductImageService {

    private final ImageUploader imageUploader;

    public ProductImageService(ImageUploader imageUploader) {
        this.imageUploader = imageUploader;
    }

    @Transactional
    public ProductImageDTO uploadProductImage(Long productId, ImageUploadCommand command) {
        return imageUploader.uploadProductImageDTO(productId, command);
    }

    @Transactional
    public void deleteProductImage(Long imageId) {
        imageUploader.deleteProductImage(imageId);
    }

    @Transactional
    public void setPrimaryImage(Long productId, Long imageId) {
        imageUploader.setPrimaryImage(productId, imageId);
    }

    @Transactional
    public void reorderImages(Long productId, List<Long> imageIds) {
        imageUploader.reorderImages(productId, imageIds);
    }
}
