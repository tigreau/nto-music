package com.musicshop.service.brand;

import com.musicshop.dto.brand.BrandDTO;
import java.util.List;

public interface BrandService {
    List<BrandDTO> findAll();
}
