package com.musicshop.controller.product;

import com.musicshop.dto.product.DetailedProductDTO;
import com.musicshop.dto.product.ProductPatchRequest;
import com.musicshop.dto.product.ProductUpsertRequest;
import com.musicshop.dto.product.SimpleProductDTO;

import com.musicshop.application.product.ProductUseCase;
import com.musicshop.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {

    private final ProductUseCase productUseCase;

    @Autowired
    public ProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    /**
     * List products with filtering, sorting, and pagination.
     *
     * GET
     * /api/products?q=strat&category=guitars&brand=fender,gibson&minPrice=100&maxPrice=1000
     * &condition=EXCELLENT,GOOD&sort=recommended&page=0&size=20
     */
    @GetMapping
    public ResponseEntity<Page<SimpleProductDTO>> listProducts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String condition,
            @RequestParam(defaultValue = "recommended") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<String> brandSlugs = brand != null
                ? Arrays.asList(brand.split(","))
                : Collections.emptyList();

        Page<SimpleProductDTO> products = productUseCase.listProducts(
                q, category, brandSlugs, minPrice, maxPrice, condition, sort, page, size);

        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetailedProductDTO> getProductById(@PathVariable Long id) {
        return productUseCase.getDetailedProductById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DetailedProductDTO> createProduct(@Valid @RequestBody ProductUpsertRequest request) {
        DetailedProductDTO newProduct = productUseCase.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DetailedProductDTO> updateProduct(@PathVariable Long id,
            @Valid @RequestBody ProductUpsertRequest request) {
        return productUseCase.updateProduct(id, request)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DetailedProductDTO> partialUpdateProduct(@PathVariable Long id,
            @Valid @RequestBody ProductPatchRequest request) {
        return productUseCase.partialUpdateProduct(id, request)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete product")
    @ApiResponse(responseCode = "204", description = "No Content")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productUseCase.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/apply-discount")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DetailedProductDTO> applyDiscount(@PathVariable Long id, @RequestParam @NotBlank String discountType) {
        return productUseCase.applyDiscount(id, discountType)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
}
