package com.ordertracker.exception;

import org.springframework.http.HttpStatus;

public class InvalidOrderStatusException extends BusinessException {

    public InvalidOrderStatusException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}