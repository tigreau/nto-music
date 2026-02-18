package com.musicshop.dto.product;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ReorderImagesRequest {

    @NotEmpty(message = "Image ID list cannot be empty.")
    @Valid
    private List<@NotNull(message = "Image ID cannot be null.") Long> imageIds;

    public List<Long> getImageIds() {
        return imageIds;
    }

    public void setImageIds(List<Long> imageIds) {
        this.imageIds = imageIds;
    }
}
