package com.musicshop.repository.product;

import com.musicshop.model.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Override
    @EntityGraph(attributePaths = { "category", "brand" })
    Page<Product> findAll(@Nullable Specification<Product> spec, Pageable pageable);

    Optional<Product> findByName(String productName);

    Optional<Product> findBySlug(String slug);

    void deleteByName(String productName);
}
