package com.musicshop.controller.brand;

import com.musicshop.application.brand.BrandUseCase;
import com.musicshop.dto.brand.BrandDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandUseCase brandUseCase;

    @Autowired
    public BrandController(BrandUseCase brandUseCase) {
        this.brandUseCase = brandUseCase;
    }

    /**
     * Get all brands for the filter UI sidebar.
     */
    @GetMapping
    public ResponseEntity<List<BrandDTO>> getAllBrands() {
        return ResponseEntity.ok(brandUseCase.findAll());
    }
}
