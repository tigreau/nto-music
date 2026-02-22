package com.musicshop.mapper;

import com.musicshop.dto.product.DetailedProductDTO;
import com.musicshop.dto.product.SimpleProductDTO;
import com.musicshop.model.product.Product;
import com.musicshop.model.product.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMapperConfig.class, uses = { BrandMapper.class, CategoryMapper.class })
public interface ProductMapper {

    @Mapping(target = "brandName", source = "brand.name")
    @Mapping(target = "categoryName", source = "category.categoryName")
    @Mapping(target = "isPromoted", ignore = true)
    SimpleProductDTO toSimpleProductDTO(Product product);

    @Mapping(target = "brandName", source = "brand.name")
    @Mapping(target = "categoryName", source = "category.categoryName")
    @Mapping(target = "isPromoted", ignore = true)
    DetailedProductDTO toDetailedProductDTO(Product product);

    @Mapping(target = "isPrimary", ignore = true)
    com.musicshop.dto.product.ProductImageDTO toProductImageDTO(ProductImage productImage);
}
