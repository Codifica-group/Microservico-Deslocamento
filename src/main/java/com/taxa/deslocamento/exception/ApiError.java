package com.taxa.deslocamento.exception;

import java.time.LocalDateTime;

public class ApiError {

    private LocalDateTime timestamp;
    private String error;
    private String message;

    public ApiError(String error, String message) {
        this.timestamp = LocalDateTime.now();
        this.error = error;
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}