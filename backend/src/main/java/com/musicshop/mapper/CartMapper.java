package com.musicshop.mapper;

import com.musicshop.dto.cart.CartItemDTO;
import com.musicshop.dto.cart.CartProductDTO;
import com.musicshop.model.cart.CartDetail;
import com.musicshop.model.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(config = CentralMapperConfig.class, uses = { BrandMapper.class, CategoryMapper.class })
public interface CartMapper {

    @Mapping(target = "subTotal", expression = "java(calculateSubTotal(detail))")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "withSubTotal", ignore = true)
    CartItemDTO toCartItemDTO(CartDetail detail);

    List<CartItemDTO> toCartItemDTOs(List<CartDetail> details);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "brand", source = "brand")
    CartProductDTO toCartProductDTO(Product product);

    default BigDecimal calculateSubTotal(CartDetail detail) {
        if (detail.getProduct() == null || detail.getProduct().getPrice() == null) {
            return null;
        }
        return detail.getProduct().getPrice().multiply(new BigDecimal(detail.getQuantity()));
    }
}
