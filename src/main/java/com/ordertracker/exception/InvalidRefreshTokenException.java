package com.ordertracker.exception;

import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends BusinessException {

    public InvalidRefreshTokenException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}