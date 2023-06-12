package com.demo.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidArgumentException extends RuntimeException{

    private final HttpStatus status;

    public InvalidArgumentException(String message, HttpStatus status){
        super(message);
        this.status = status;
    }
}
