package com.musicshop.dto.error;

public record ApiErrorResponse(
        String timestamp,
        int status,
        String error,
        String code,
        String message) {

    public String getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
