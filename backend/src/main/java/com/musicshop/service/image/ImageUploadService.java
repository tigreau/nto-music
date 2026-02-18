package com.musicshop.service.image;

import com.musicshop.model.product.Product;
import com.musicshop.model.product.ProductImage;
import com.musicshop.repository.product.ProductImageRepository;
import com.musicshop.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageUploadService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public ImageUploadService(ProductRepository productRepository,
            ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadDir, e);
        }
    }

    @Transactional
    public ProductImage uploadProductImage(Long productId, MultipartFile file,
            String altText, boolean isPrimary) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        validateImageFile(file);

        try {
            // Ensure directory exists
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String filename = "product_" + productId + "_" + UUID.randomUUID() + extension;

            // Save file
            Path targetPath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // If this is primary, unset other primary images
            if (isPrimary) {
                product.getImages().forEach(img -> {
                    img.setPrimary(false);
                    productImageRepository.save(img);
                });
            }

            // Create ProductImage entity
            ProductImage productImage = new ProductImage();
            productImage.setProduct(product);
            productImage.setUrl("/uploads/" + filename);
            productImage.setAltText(altText != null && !altText.isBlank() ? altText : product.getName());
            productImage.setPrimary(isPrimary);
            productImage.setDisplayOrder(product.getImages().size());

            return productImageRepository.save(productImage);

        } catch (IOException e) {
            e.printStackTrace(); // Log error to console
            throw new RuntimeException("Failed to save image file: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteProductImage(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
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
            throw new RuntimeException("Failed to delete image file", e);
        }
    }

    @Transactional
    public void setPrimaryImage(Long productId, Long imageId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        boolean imageBelongsToProduct = product.getImages().stream()
                .anyMatch(img -> img.getId().equals(imageId));
        if (!imageBelongsToProduct) {
            throw new RuntimeException("Image not found for product");
        }

        // Unset all primary flags
        product.getImages().forEach(img -> {
            img.setPrimary(img.getId().equals(imageId));
            productImageRepository.save(img);
        });
    }

    @Transactional
    public void reorderImages(Long productId, List<Long> imageIds) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        for (int i = 0; i < imageIds.size(); i++) {
            Long imageId = imageIds.get(i);
            ProductImage image = product.getImages().stream()
                    .filter(img -> img.getId().equals(imageId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Image not found: " + imageId));

            image.setDisplayOrder(i);
            // First image in the list becomes the primary image
            image.setPrimary(i == 0);
            productImageRepository.save(image);
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }

        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("File size exceeds maximum of 10MB");
        }
    }

    private void normalizeImageStateAfterDelete(Long productId, boolean deletedWasPrimary) {
        List<ProductImage> remaining = new ArrayList<>(productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId));
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
