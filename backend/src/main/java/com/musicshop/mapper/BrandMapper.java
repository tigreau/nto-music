package com.musicshop.mapper;

import com.musicshop.dto.brand.BrandDTO;
import com.musicshop.model.brand.Brand;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface BrandMapper {
    BrandDTO toBrandDTO(Brand brand);

    Brand toBrand(BrandDTO brandDTO);
}
