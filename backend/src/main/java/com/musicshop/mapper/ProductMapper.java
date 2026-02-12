package com.musicshop.mapper;

import com.musicshop.dto.product.DetailedProductDTO;
import com.musicshop.dto.product.SimpleProductDTO;
import com.musicshop.model.product.Product;
import com.musicshop.model.product.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { BrandMapper.class, CategoryMapper.class })
public interface ProductMapper {

    @Mapping(target = "brandName", source = "brand.name")
    @Mapping(target = "categoryName", source = "category.categoryName")
    SimpleProductDTO toSimpleProductDTO(Product product);

    @Mapping(target = "brandName", source = "brand.name")
    @Mapping(target = "categoryName", source = "category.categoryName")
    DetailedProductDTO toDetailedProductDTO(Product product);

    DetailedProductDTO.ProductImageDTO toProductImageDTO(ProductImage productImage);
}
