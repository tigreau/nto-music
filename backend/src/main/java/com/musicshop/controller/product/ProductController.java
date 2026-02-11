package com.musicshop.controller.product;

import com.musicshop.dto.product.DetailedProductDTO;
import com.musicshop.dto.product.SimpleProductDTO;
import com.musicshop.dto.product.ProductDTOFactory;
import com.musicshop.model.product.Product;
import com.musicshop.model.product.ProductCondition;
import com.musicshop.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * List products with filtering, sorting, and pagination.
     *
     * GET
     * /api/products?category=guitars&brand=fender,gibson&minPrice=100&maxPrice=1000
     * &condition=EXCELLENT,GOOD&sort=recommended&page=0&size=20
     */
    @GetMapping
    public ResponseEntity<Page<SimpleProductDTO>> listProducts(
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

        List<ProductCondition> conditions = condition != null
                ? Arrays.stream(condition.split(","))
                        .map(ProductCondition::valueOf)
                        .collect(Collectors.toList())
                : Collections.emptyList();

        Page<SimpleProductDTO> products = productService.listProducts(
                category, brandSlugs, minPrice, maxPrice, conditions, sort, page, size);

        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetailedProductDTO> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(product -> ResponseEntity.ok(ProductDTOFactory.createDetailedProductDTO(product)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        try {
            Product newProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetailedProductDTO> updateProduct(@PathVariable Long id,
            @RequestBody Product productDetails) {
        return productService.updateProduct(id, productDetails)
                .map(product -> ResponseEntity.ok(ProductDTOFactory.createDetailedProductDTO(product)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DetailedProductDTO> partialUpdateProduct(@PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        return productService.partialUpdateProduct(id, updates)
                .map(product -> ResponseEntity.ok(ProductDTOFactory.createDetailedProductDTO(product)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        boolean exists = productService.getProductById(id).isPresent();
        if (exists) {
            productService.deleteProduct(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/apply-discount")
    public ResponseEntity<?> applyDiscount(@PathVariable Long id, @RequestParam String discountType) {
        return productService.applyDiscount(id, discountType)
                .map(product -> ResponseEntity.ok(ProductDTOFactory.createDetailedProductDTO(product)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
