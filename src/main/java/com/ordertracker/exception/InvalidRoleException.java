package com.ordertracker.exception;

import org.springframework.http.HttpStatus;

public class InvalidRoleException extends BusinessException {

    public InvalidRoleException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}