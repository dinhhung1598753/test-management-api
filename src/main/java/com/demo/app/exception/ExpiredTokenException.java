package com.demo.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExpiredTokenException extends BaseException {

    public ExpiredTokenException(String message, HttpStatus status){
        super(message, status);

    }

}
