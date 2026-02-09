package com.musicshop.repository.category;

import com.musicshop.model.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @org.springframework.data.jpa.repository.Query("SELECT new com.musicshop.dto.category.CategoryDTO(c.id, c.categoryName, COUNT(p)) "
            +
            "FROM Category c LEFT JOIN Product p ON p.category = c GROUP BY c.id, c.categoryName")
    java.util.List<com.musicshop.dto.category.CategoryDTO> findAllWithProductCount();

    java.util.Optional<Category> findByCategoryName(String categoryName);
}
