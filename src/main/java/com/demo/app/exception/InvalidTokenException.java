package com.demo.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidTokenException extends BaseException {

    public InvalidTokenException(String message, HttpStatus status){
        super(message, status);

    }

}
