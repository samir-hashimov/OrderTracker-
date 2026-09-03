package com.ordertracker.exception;

import org.springframework.http.HttpStatus;

public class DuplicateWebhookException extends BusinessException {

    public DuplicateWebhookException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}