package com.demo.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserNotSignInException extends RuntimeException {

    private final HttpStatus status;

    public UserNotSignInException(String message, HttpStatus status){
        super(message);
        this.status = status;
    }

}