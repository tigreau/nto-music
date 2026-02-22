package com.musicshop.application.product;

import com.musicshop.dto.product.DetailedProductDTO;
import com.musicshop.dto.product.ProductPatchRequest;
import com.musicshop.dto.product.ProductUpsertRequest;
import com.musicshop.dto.product.SimpleProductDTO;
import com.musicshop.service.product.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
public class ProductUseCase {

    private final ProductService productService;

    public ProductUseCase(ProductService productService) {
        this.productService = productService;
    }

    public Page<SimpleProductDTO> listProducts(
            String query,
            String category,
            List<String> brandSlugs,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String condition,
            String sort,
            int page,
            int size) {
        return productService.listProducts(query, category, brandSlugs, minPrice, maxPrice, condition, sort, page,
                size);
    }

    public Optional<DetailedProductDTO> getDetailedProductById(Long id) {
        return productService.getDetailedProductById(id);
    }

    public DetailedProductDTO createProduct(ProductUpsertRequest request) {
        return productService.createProduct(request);
    }

    public Optional<DetailedProductDTO> updateProduct(Long id, ProductUpsertRequest request) {
        return productService.updateProduct(id, request);
    }

    public Optional<DetailedProductDTO> partialUpdateProduct(Long id, ProductPatchRequest request) {
        return productService.partialUpdateProduct(id, request);
    }

    public boolean existsById(Long id) {
        return productService.existsById(id);
    }

    public void deleteProduct(Long id) {
        productService.deleteProduct(id);
    }

    public Optional<DetailedProductDTO> applyDiscount(Long id, String discountType) {
        return productService.applyDiscount(id, discountType);
    }
}
