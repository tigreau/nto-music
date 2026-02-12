package com.musicshop.mapper;

import com.musicshop.dto.cart.CartItemDTO;
import com.musicshop.dto.cart.CartProductDTO;
import com.musicshop.model.cart.CartDetail;
import com.musicshop.model.product.Product;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", uses = { BrandMapper.class, CategoryMapper.class })
public interface CartMapper {

    @Mapping(target = "subTotal", ignore = true)
    @Mapping(target = "product", source = "product")
    CartItemDTO toCartItemDTO(CartDetail detail);

    List<CartItemDTO> toCartItemDTOs(List<CartDetail> details);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "brand", source = "brand")
    CartProductDTO toCartProductDTO(Product product);

    @AfterMapping
    default void calculateSubTotal(CartDetail detail, @MappingTarget CartItemDTO dto) {
        if (detail.getProduct() != null && detail.getProduct().getPrice() != null && dto.getQuantity() != null) {
            dto.setSubTotal(detail.getProduct().getPrice().multiply(new BigDecimal(dto.getQuantity())));
        }
    }
}
