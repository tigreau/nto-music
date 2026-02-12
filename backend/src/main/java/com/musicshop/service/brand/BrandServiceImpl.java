package com.musicshop.service.brand;

import com.musicshop.dto.brand.BrandDTO;
import com.musicshop.mapper.BrandMapper;
import com.musicshop.repository.brand.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository, BrandMapper brandMapper) {
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
    }

    @Override
    public List<BrandDTO> findAll() {
        return brandRepository.findAll().stream()
                .map(brandMapper::toBrandDTO)
                .collect(Collectors.toList());
    }
}
