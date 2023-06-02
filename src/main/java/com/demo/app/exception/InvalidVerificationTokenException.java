package com.demo.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidVerificationTokenException extends RuntimeException{

    private final HttpStatus status;

    public InvalidVerificationTokenException(String message, HttpStatus status){
        super(message);
        this.status = status;
    }

}
