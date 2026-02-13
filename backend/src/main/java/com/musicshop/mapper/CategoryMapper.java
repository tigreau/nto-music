package com.musicshop.mapper;

import com.musicshop.dto.category.CategoryDTO;
import com.musicshop.model.category.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "productCount", ignore = true)
    @Mapping(target = "name", source = "categoryName")
    CategoryDTO toCategoryDTO(Category category);

    @Mapping(target = "categoryName", source = "name")
    Category toCategory(CategoryDTO categoryDTO);
}
