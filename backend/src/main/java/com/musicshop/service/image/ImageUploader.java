package com.musicshop.service.image;

import com.musicshop.dto.product.ImageUploadCommand;
import com.musicshop.dto.product.ProductImageDTO;

import java.util.List;

public interface ImageUploader {

    ProductImageDTO uploadProductImageDTO(Long productId, ImageUploadCommand command);

    void deleteProductImage(Long imageId);

    void setPrimaryImage(Long productId, Long imageId);

    void reorderImages(Long productId, List<Long> imageIds);
}
