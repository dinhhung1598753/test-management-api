package com.demo.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OperationNotSupportedException extends RuntimeException {

    private final HttpStatus status;

    public OperationNotSupportedException(String message, HttpStatus status){
        super(message);
        this.status = status;
    }

}
