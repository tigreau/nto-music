package com.musicshop.dto.user;

public record NotificationDTO(
        Long id,
        String message,
        String type,
        String timestamp,
        boolean read,
        Long relatedEntityId) {

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public Long getRelatedEntityId() {
        return relatedEntityId;
    }
}
