package com.musicshop.mapper;

import com.musicshop.dto.brand.BrandDTO;
import com.musicshop.dto.cart.CartItemDTO;
import com.musicshop.dto.cart.CartProductDTO;
import com.musicshop.dto.category.CategoryDTO;
import com.musicshop.model.brand.Brand;
import com.musicshop.model.cart.CartDetail;
import com.musicshop.model.category.Category;
import com.musicshop.model.product.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    public CartItemDTO toCartItemDTO(CartDetail detail) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(detail.getId());
        dto.setQuantity(detail.getQuantity());

        // Calculate subtotal if price exists
        if (detail.getProduct() != null && detail.getProduct().getPrice() != null) {
            dto.setSubTotal(detail.getProduct().getPrice().multiply(new BigDecimal(detail.getQuantity())));
        }

        dto.setProduct(toCartProductDTO(detail.getProduct()));
        return dto;
    }

    public List<CartItemDTO> toCartItemDTOs(List<CartDetail> details) {
        return details.stream()
                .map(this::toCartItemDTO)
                .collect(Collectors.toList());
    }

    private CartProductDTO toCartProductDTO(Product product) {
        if (product == null)
            return null;

        CartProductDTO dto = new CartProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());
        dto.setPrice(product.getPrice());
        dto.setThumbnailUrl(product.getThumbnailUrl());

        if (product.getCategory() != null) {
            dto.setCategory(toCategoryDTO(product.getCategory()));
        }

        if (product.getBrand() != null) {
            dto.setBrand(toBrandDTO(product.getBrand()));
        }

        return dto;
    }

    private CategoryDTO toCategoryDTO(Category category) {
        if (category == null)
            return null;
        // Construct CategoryDTO with simplified constructor for Cart
        return new CategoryDTO(category.getId(), category.getCategoryName(), category.getSlug());
    }

    private BrandDTO toBrandDTO(Brand brand) {
        if (brand == null)
            return null;
        return new BrandDTO(brand.getId(), brand.getName(), brand.getSlug(), brand.getLogoUrl());
    }
}
