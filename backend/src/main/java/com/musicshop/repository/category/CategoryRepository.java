package com.musicshop.repository.category;

import com.musicshop.model.category.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

        @Query("SELECT new com.musicshop.dto.category.CategoryDTO(c.id, c.categoryName, c.slug, COUNT(p)) "
                        + "FROM Category c LEFT JOIN Product p ON p.category = c "
                        + "WHERE c.parentCategory IS NULL "
                        + "GROUP BY c.id, c.categoryName, c.slug")
        List<com.musicshop.dto.category.CategoryDTO> findAllWithProductCount();

        @Query("SELECT new com.musicshop.dto.category.CategoryDTO(c.id, c.categoryName, c.slug, COUNT(p)) "
                        + "FROM Category c LEFT JOIN Product p ON p.category = c "
                        + "WHERE c.parentCategory = :parent "
                        + "GROUP BY c.id, c.categoryName, c.slug")
        List<com.musicshop.dto.category.CategoryDTO> findSubcategoriesWithProductCount(
                        @Param("parent") Category parent);

        Optional<Category> findByCategoryName(String categoryName);

        Optional<Category> findBySlug(String slug);

        List<Category> findByParentCategory(Category parent);

        List<Category> findByParentCategorySlug(String parentSlug);
}
