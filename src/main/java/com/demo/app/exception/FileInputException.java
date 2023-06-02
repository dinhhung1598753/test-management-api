package com.demo.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Getter
public class FileInputException extends IOException {

    private final HttpStatus status;

    public FileInputException(String message, HttpStatus status){
        super(message);
        this.status = status;
    }
}
