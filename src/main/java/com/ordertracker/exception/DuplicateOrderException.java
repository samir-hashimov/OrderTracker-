package com.ordertracker.exception;

import org.springframework.http.HttpStatus;

public class DuplicateOrderException extends BusinessException {

    public DuplicateOrderException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}