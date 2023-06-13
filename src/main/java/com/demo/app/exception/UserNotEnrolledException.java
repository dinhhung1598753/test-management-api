package com.demo.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserNotEnrolledException  extends RuntimeException{

    private final HttpStatus status;

    public UserNotEnrolledException(String message, HttpStatus status){
        super(message);
        this.status = status;
    }
}
