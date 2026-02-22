package com.musicshop.mapper;

import com.musicshop.dto.product.ImageUploadCommand;
import com.musicshop.exception.ImageStorageException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class ProductImageUploadMapper {

    public ImageUploadCommand toCommand(MultipartFile file, String altText, boolean isPrimary) {
        try {
            return new ImageUploadCommand(
                    file.getBytes(),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    altText,
                    isPrimary
            );
        } catch (IOException e) {
            throw new ImageStorageException("Failed to read uploaded image content", e);
        }
    }
}
