package com.musicshop.dto.product;

import com.musicshop.model.product.ProductCondition;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class ProductUpsertRequest {

    @NotBlank(message = "Product name is required.")
    @Pattern(regexp = "^[a-zA-Z0-9 \\-'\".,()]+$", message = "Product name contains invalid characters.")
    private String name;

    @NotBlank(message = "Product description is required.")
    private String description;

    @NotNull(message = "Product price is required.")
    @Positive(message = "Product price must be greater than 0.")
    @DecimalMax(value = "10000.00", message = "Product price is unrealistically high.")
    private BigDecimal price;

    @Min(value = 0, message = "Quantity cannot be negative.")
    private int quantityAvailable;

    @NotNull(message = "Product category is required.")
    private Long categoryId;

    @NotNull(message = "Product condition is required.")
    private ProductCondition condition;

    private String conditionNotes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public ProductCondition getCondition() {
        return condition;
    }

    public void setCondition(ProductCondition condition) {
        this.condition = condition;
    }

    public String getConditionNotes() {
        return conditionNotes;
    }

    public void setConditionNotes(String conditionNotes) {
        this.conditionNotes = conditionNotes;
    }
}
