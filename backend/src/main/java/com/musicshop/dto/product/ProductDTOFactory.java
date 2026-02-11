package com.musicshop.dto.product;

import com.musicshop.model.product.Product;

import java.util.Collections;
import java.util.stream.Collectors;

public class ProductDTOFactory {

    public static SimpleProductDTO createSimpleProductDTO(Product product) {
        return new SimpleProductDTO.Builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .price(product.getPrice())
                .categoryName(product.getCategory() != null ? product.getCategory().getCategoryName() : null)
                .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
                .condition(product.getCondition())
                .thumbnailUrl(product.getThumbnailUrl())
                .isPromoted(product.isPromoted())
                .build();
    }

    public static DetailedProductDTO createDetailedProductDTO(Product product) {
        return new DetailedProductDTO.Builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantityAvailable(product.getQuantityAvailable())
                .categoryName(product.getCategory() != null ? product.getCategory().getCategoryName() : null)
                .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
                .condition(product.getCondition())
                .conditionNotes(product.getConditionNotes())
                .isPromoted(product.isPromoted())
                .images(product.getImages() != null
                        ? product.getImages().stream()
                                .map(img -> new DetailedProductDTO.ProductImageDTO(
                                        img.getId(), img.getUrl(), img.getAltText(), img.isPrimary()))
                                .collect(Collectors.toList())
                        : Collections.emptyList())
                .build();
    }
}
