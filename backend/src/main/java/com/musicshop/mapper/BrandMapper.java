package com.musicshop.mapper;

import com.musicshop.dto.brand.BrandDTO;
import com.musicshop.model.brand.Brand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    BrandDTO toBrandDTO(Brand brand);

    Brand toBrand(BrandDTO brandDTO);
}
