package com.musicshop.controller.product;

import com.musicshop.model.product.ProductImage;
import com.musicshop.service.image.ImageUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/images")
public class ProductImageController {

    private final ImageUploadService imageUploadService;

    public ProductImageController(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }

    @PostMapping
    public ResponseEntity<?> uploadImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String altText,
            @RequestParam(defaultValue = "false") boolean isPrimary) {

        try {
            ProductImage image = imageUploadService.uploadProductImage(
                    productId, file, altText, isPrimary);
            return ResponseEntity.ok(image);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Log stack trace
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
        try {
            imageUploadService.deleteProductImage(imageId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/{imageId}/primary")
    public ResponseEntity<?> setPrimaryImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        try {
            imageUploadService.setPrimaryImage(productId, imageId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/reorder")
    public ResponseEntity<?> reorderImages(
            @PathVariable Long productId,
            @RequestBody List<Long> imageIds) {
        try {
            imageUploadService.reorderImages(productId, imageIds);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
