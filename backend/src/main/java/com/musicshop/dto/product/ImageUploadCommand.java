package com.musicshop.dto.product;

public class ImageUploadCommand {
    private final byte[] bytes;
    private final String originalFilename;
    private final String contentType;
    private final long size;
    private final String altText;
    private final boolean primary;

    public ImageUploadCommand(byte[] bytes,
                              String originalFilename,
                              String contentType,
                              long size,
                              String altText,
                              boolean primary) {
        this.bytes = bytes;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.size = size;
        this.altText = altText;
        this.primary = primary;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSize() {
        return size;
    }

    public String getAltText() {
        return altText;
    }

    public boolean isPrimary() {
        return primary;
    }
}
