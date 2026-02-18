package com.musicshop.application.brand;

import com.musicshop.dto.brand.BrandDTO;
import com.musicshop.service.brand.BrandService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BrandUseCase {

    private final BrandService brandService;

    public BrandUseCase(BrandService brandService) {
        this.brandService = brandService;
    }

    public List<BrandDTO> findAll() {
        return brandService.findAll();
    }
}
