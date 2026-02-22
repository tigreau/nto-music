package com.musicshop.mapper;

import com.musicshop.dto.category.CategoryDTO;
import com.musicshop.model.category.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMapperConfig.class)
public interface CategoryMapper {
    @Mapping(target = "productCount", ignore = true)
    @Mapping(target = "name", source = "categoryName")
    @Mapping(target = "withDescription", ignore = true)
    @Mapping(target = "withProductCount", ignore = true)
    @Mapping(target = "withSubCategories", ignore = true)
    CategoryDTO toCategoryDTO(Category category);

    @Mapping(target = "categoryName", source = "name")
    @Mapping(target = "iconUrl", ignore = true)
    @Mapping(target = "displayOrder", ignore = true)
    @Mapping(target = "parentCategory", ignore = true)
    Category toCategory(CategoryDTO categoryDTO);
}
