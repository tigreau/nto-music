package com.musicshop.mapper;

import com.musicshop.dto.product.ProductImageDTO;
import com.musicshop.model.product.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMapperConfig.class)
public interface ProductImageMapper {

    @Mapping(target = "isPrimary", ignore = true)
    ProductImageDTO toProductImageDTO(ProductImage productImage);
}
