package com.musicshop.service.category;

import com.musicshop.dto.category.CategoryDTO;
import com.musicshop.model.category.Category;
import com.musicshop.repository.category.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @javax.annotation.PostConstruct
    public void seedCategories() {
        String[] defaults = { "Guitars", "Drums", "Keys", "Studio", "DJ", "Mics", "Software", "Accessories",
                "Traditional" };
        for (String name : defaults) {
            if (categoryRepository.findByCategoryName(name).isEmpty()) {
                Category cat = new Category();
                cat.setCategoryName(name);
                categoryRepository.save(cat);
            }
        }
    }

    @Override
    public List<CategoryDTO> findAllProperties() {
        return categoryRepository.findAllWithProductCount();
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
