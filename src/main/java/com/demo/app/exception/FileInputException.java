package com.demo.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FileInputException extends RuntimeException {

    private final HttpStatus status;

    public FileInputException(String message, HttpStatus status){
        super(message);
        this.status = status;
    }
}
