package com.musicshop.infrastructure.image;

import com.musicshop.dto.product.ProductImageDTO;
import com.musicshop.dto.product.ImageUploadCommand;
import com.musicshop.exception.ImageStorageException;
import com.musicshop.exception.ResourceNotFoundException;
import com.musicshop.exception.ValidationException;
import com.musicshop.mapper.ProductImageMapper;
import com.musicshop.model.product.Product;
import com.musicshop.model.product.ProductImage;
import com.musicshop.repository.product.ProductImageRepository;
import com.musicshop.repository.product.ProductRepository;
import com.musicshop.service.image.ImageUploader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageUploadService implements ImageUploader {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductImageMapper productImageMapper;

    public ImageUploadService(ProductRepository productRepository,
            ProductImageRepository productImageRepository,
            ProductImageMapper productImageMapper) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.productImageMapper = productImageMapper;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new ImageStorageException("Could not create upload directory: " + uploadDir, e);
        }
    }

    public ProductImage uploadProductImage(Long productId, ImageUploadCommand command) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        validateImageFile(command);

        try {
            // Ensure directory exists
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = command.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String filename = "product_" + productId + "_" + UUID.randomUUID() + extension;

            // Save file
            Path targetPath = uploadPath.resolve(filename);
            Files.write(targetPath, command.getBytes(), StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            // If this is primary, unset other primary images
            if (command.isPrimary()) {
                product.getImages().forEach(img -> {
                    img.setPrimary(false);
                    productImageRepository.save(img);
                });
            }

            // Create ProductImage entity
            ProductImage productImage = new ProductImage();
            productImage.setProduct(product);
            productImage.setUrl("/uploads/" + filename);
            productImage.setAltText(command.getAltText() != null && !command.getAltText().isBlank()
                    ? command.getAltText()
                    : product.getName());
            productImage.setPrimary(command.isPrimary());
            productImage.setDisplayOrder(product.getImages().size());

            return productImageRepository.save(productImage);

        } catch (IOException e) {
            throw new ImageStorageException("Failed to save image file", e);
        }
    }

    public ProductImageDTO uploadProductImageDTO(Long productId, ImageUploadCommand command) {
        ProductImage image = uploadProductImage(productId, command);
        return productImageMapper.toProductImageDTO(image);
    }

    public void deleteProductImage(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));
        Long productId = image.getProduct().getId();
        boolean deletedWasPrimary = image.isPrimary();

        try {
            // Delete file from filesystem
            String filename = image.getUrl().replace("/uploads/", "");
            Path filePath = Paths.get(uploadDir).resolve(filename);
            Files.deleteIfExists(filePath);

            // Delete from database
            productImageRepository.delete(image);
            normalizeImageStateAfterDelete(productId, deletedWasPrimary);

        } catch (IOException e) {
            throw new ImageStorageException("Failed to delete image file", e);
        }
    }

    public void setPrimaryImage(Long productId, Long imageId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        boolean imageBelongsToProduct = product.getImages().stream()
                .anyMatch(img -> img.getId().equals(imageId));
        if (!imageBelongsToProduct) {
            throw new ResourceNotFoundException("Image not found for product");
        }

        // Unset all primary flags
        product.getImages().forEach(img -> {
            img.setPrimary(img.getId().equals(imageId));
            productImageRepository.save(img);
        });
    }

    public void reorderImages(Long productId, List<Long> imageIds) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        for (int i = 0; i < imageIds.size(); i++) {
            Long imageId = imageIds.get(i);
            ProductImage image = product.getImages().stream()
                    .filter(img -> img.getId().equals(imageId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Image not found: " + imageId));

            image.setDisplayOrder(i);
            // First image in the list becomes the primary image
            image.setPrimary(i == 0);
            productImageRepository.save(image);
        }
    }

    private void validateImageFile(ImageUploadCommand command) {
        if (command.getBytes() == null || command.getBytes().length == 0) {
            throw new ValidationException("File is empty");
        }

        String contentType = command.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ValidationException("File must be an image");
        }

        long maxSize = 10 * 1024 * 1024; // 10MB
        if (command.getSize() > maxSize) {
            throw new ValidationException("File size exceeds maximum of 10MB");
        }
    }

    private void normalizeImageStateAfterDelete(Long productId, boolean deletedWasPrimary) {
        List<ProductImage> remaining = new ArrayList<>(
                productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId));
        if (remaining.isEmpty()) {
            return;
        }

        boolean hasPrimary = remaining.stream().anyMatch(ProductImage::isPrimary);
        boolean shouldPromoteFirst = deletedWasPrimary || !hasPrimary;

        for (int i = 0; i < remaining.size(); i++) {
            ProductImage current = remaining.get(i);
            current.setDisplayOrder(i);
            if (shouldPromoteFirst) {
                current.setPrimary(i == 0);
            }
            productImageRepository.save(current);
        }
    }

}
