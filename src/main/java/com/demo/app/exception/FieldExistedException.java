package com.demo.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FieldExistedException extends RuntimeException{

    private final HttpStatus status;

    public FieldExistedException(String message, HttpStatus status){
        super(message);
        this.status = status;
    }
}
