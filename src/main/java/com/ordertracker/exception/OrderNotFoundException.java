package com.ordertracker.exception;

import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends BusinessException {

    public OrderNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}