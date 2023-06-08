package com.demo.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidRoleException extends RuntimeException {

    private final HttpStatus status;

    public InvalidRoleException(String message, HttpStatus status){
        super(message);
        this.status = status;
    }
}
