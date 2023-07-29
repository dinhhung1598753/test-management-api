package com.demo.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DuplicatedUniqueValueException extends RuntimeException{

    private final HttpStatus status;

    public DuplicatedUniqueValueException(String message, HttpStatus status){
        super(message);
        this.status = status;
    }
}
