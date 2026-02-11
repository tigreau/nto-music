package com.musicshop.specification;

import com.musicshop.model.product.Product;
import com.musicshop.model.product.ProductCondition;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.List;

public class ProductSpecification {

    /**
     * Filter by category slug. Matches products whose category slug equals the
     * given slug,
     * OR whose parent category slug equals the given slug (so filtering by a parent
     * category returns all subcategory products too).
     */
    public static Specification<Product> hasCategory(String categorySlug) {
        return (root, query, cb) -> {
            if (categorySlug == null || categorySlug.isBlank())
                return null;
            var categoryJoin = root.join("category");
            Predicate directMatch = cb.equal(categoryJoin.get("slug"), categorySlug);
            Predicate parentMatch = cb.equal(
                    categoryJoin.get("parentCategory").get("slug"), categorySlug);
            return cb.or(directMatch, parentMatch);
        };
    }

    public static Specification<Product> hasBrands(List<String> brandSlugs) {
        return (root, query, cb) -> {
            if (brandSlugs == null || brandSlugs.isEmpty())
                return null;
            return root.join("brand").get("slug").in(brandSlugs);
        };
    }

    public static Specification<Product> hasMinPrice(BigDecimal minPrice) {
        return (root, query, cb) -> {
            if (minPrice == null)
                return null;
            return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
        };
    }

    public static Specification<Product> hasMaxPrice(BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (maxPrice == null)
                return null;
            return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    public static Specification<Product> hasConditions(List<ProductCondition> conditions) {
        return (root, query, cb) -> {
            if (conditions == null || conditions.isEmpty())
                return null;
            return root.get("condition").in(conditions);
        };
    }

    public static Specification<Product> inStock() {
        return (root, query, cb) -> cb.greaterThan(root.get("quantityAvailable"), 0);
    }
}
