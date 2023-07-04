package com.demo.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExpiredTokenException extends RuntimeException {

    private final HttpStatus status;

    public ExpiredTokenException(String message, HttpStatus status){
        super(message);
        this.status = status;
    }

}
