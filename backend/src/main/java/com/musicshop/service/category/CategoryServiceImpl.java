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
        // Parent categories: name, slug, description
        String[][] parents = {
                { "Guitars & Plucked", "guitars-plucked", "You pluck or strum strings." },
                { "Drums & Percussion", "drums-percussion", "You strike it." },
                { "Keys & Synths", "keys-synths", "You press keys or buttons." },
                { "Wind & Brass", "wind-brass", "You blow into it." },
                { "Bowed Strings", "bowed-strings", "You move a bow across strings." }
        };

        // Subcategories per parent: parentIndex -> { name, slug }
        String[][][] subcategories = {
                // 0: Guitars & Plucked
                {
                        { "Electric Guitar", "electric-guitar" },
                        { "Acoustic Guitar", "acoustic-guitar" },
                        { "Bass Guitar", "bass-guitar" },
                        { "Ukulele", "ukulele" },
                        { "Banjo", "banjo" },
                        { "Mandolin", "mandolin" }
                },
                // 1: Drums & Percussion
                {
                        { "Drum Kit", "drum-kit" },
                        { "Cymbal", "cymbal" },
                        { "Cajon", "cajon" },
                        { "Drum Machine", "drum-machine" },
                        { "Xylophone", "xylophone" }
                },
                // 2: Keys & Synths
                {
                        { "Piano", "piano" },
                        { "Organ", "organ" },
                        { "Accordion", "accordion" },
                        { "Synthesizer", "synthesizer" },
                        { "Sampler", "sampler" }
                },
                // 3: Wind & Brass
                {
                        { "Saxophone", "saxophone" },
                        { "Trumpet", "trumpet" },
                        { "Flute", "flute" },
                        { "Harmonica", "harmonica" },
                        { "Bagpipe", "bagpipe" },
                        { "Kazoo", "kazoo" }
                },
                // 4: Bowed Strings
                {
                        { "Violin", "violin" },
                        { "Viola", "viola" },
                        { "Cello", "cello" },
                        { "Double Bass", "double-bass" },
                        { "Electric Violin", "electric-violin" }
                }
        };

        for (int i = 0; i < parents.length; i++) {
            String pName = parents[i][0];
            String pSlug = parents[i][1];
            String pDesc = parents[i][2];
            int order = i + 1;

            Category parent = categoryRepository.findByCategoryName(pName)
                    .orElseGet(() -> {
                        Category c = new Category();
                        c.setCategoryName(pName);
                        c.setSlug(pSlug);
                        c.setDescription(pDesc);
                        return categoryRepository.save(c);
                    });

            // Ensure slug/description/order are up to date
            boolean updated = false;
            if (parent.getSlug() == null || !parent.getSlug().equals(pSlug)) {
                parent.setSlug(pSlug);
                updated = true;
            }
            if (parent.getDescription() == null || !parent.getDescription().equals(pDesc)) {
                parent.setDescription(pDesc);
                updated = true;
            }
            if (parent.getDisplayOrder() != order) {
                parent.setDisplayOrder(order);
                updated = true;
            }
            if (updated) {
                categoryRepository.save(parent);
            }

            // Seed subcategories
            for (String[] sub : subcategories[i]) {
                String sName = sub[0];
                String sSlug = sub[1];
                Category parentRef = parent; // for lambda

                categoryRepository.findByCategoryName(sName)
                        .orElseGet(() -> {
                            Category sc = new Category();
                            sc.setCategoryName(sName);
                            sc.setSlug(sSlug);
                            sc.setParentCategory(parentRef);
                            return categoryRepository.save(sc);
                        });
            }
        }
    }

    @Override
    public List<CategoryDTO> findAllProperties() {
        // Fetch parent categories with counts
        List<CategoryDTO> parents = categoryRepository.findAllWithProductCount();

        // For each parent, fetch subcategories with counts
        for (CategoryDTO parent : parents) {
            categoryRepository.findBySlug(parent.getSlug()).ifPresent(parentEntity -> {
                List<CategoryDTO> subs = categoryRepository.findSubcategoriesWithProductCount(parentEntity);
                parent.setSubCategories(subs);

                // Set description on parent DTO
                parent.setDescription(parentEntity.getDescription());

                // Aggregate: parent productCount = sum of subcategory counts
                long totalCount = subs.stream().mapToLong(CategoryDTO::getProductCount).sum();
                parent.setProductCount(totalCount);
            });
        }

        return parents;
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category createCategory(Category category) {
        if (category.getSlug() == null || category.getSlug().isBlank()) {
            category.setSlug(category.getCategoryName().toLowerCase().replaceAll("[^a-z0-9]+", "-"));
        }
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
