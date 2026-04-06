package com.example.notification.exception;

import java.time.OffsetDateTime;
import java.util.Map;

public class ValidationErrorResponse {

    private final OffsetDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final Map<String, String> errors;

    public ValidationErrorResponse(OffsetDateTime timestamp,
                                   int status,
                                   String error,
                                   String message,
                                   String path,
                                   Map<String, String> errors) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.errors = errors;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
