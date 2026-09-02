package com.ordertracker.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends BusinessException {

    public UserAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}