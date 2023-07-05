package com.demo.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EntityNotFoundException extends BaseException{

    public EntityNotFoundException(String message, HttpStatus status){
        super(message, status);
    }
}
