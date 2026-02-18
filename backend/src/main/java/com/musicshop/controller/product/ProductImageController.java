package com.musicshop.controller.product;

import com.musicshop.application.product.ProductImageUseCase;
import com.musicshop.dto.product.ProductImageDTO;
import com.musicshop.dto.product.ReorderImagesRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/products/{productId}/images")
public class ProductImageController {

    private final ProductImageUseCase productImageUseCase;

    public ProductImageController(ProductImageUseCase productImageUseCase) {
        this.productImageUseCase = productImageUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductImageDTO> uploadImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String altText,
            @RequestParam(defaultValue = "false") boolean isPrimary) {
        return ResponseEntity.ok(productImageUseCase.uploadImage(productId, file, altText, isPrimary));
    }

    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete product image")
    @ApiResponse(responseCode = "204", description = "No Content")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        productImageUseCase.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{imageId}/primary")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Set product primary image")
    @ApiResponse(responseCode = "204", description = "No Content")
    public ResponseEntity<Void> setPrimaryImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        productImageUseCase.setPrimaryImage(productId, imageId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reorder")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reorder product images")
    @ApiResponse(responseCode = "204", description = "No Content")
    public ResponseEntity<Void> reorderImages(
            @PathVariable Long productId,
            @Valid @RequestBody ReorderImagesRequest request) {
        productImageUseCase.reorderImages(productId, request.getImageIds());
        return ResponseEntity.noContent().build();
    }
}
