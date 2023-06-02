package com.demo.app.service;

import com.demo.app.exception.FileInputException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileStorageService {
    String createClassDirectory(String classCode) throws IOException;

    String upload(String classCode, MultipartFile file) throws FileInputException;

    void checkIfFileIsImageFormat(List<MultipartFile> file) throws FileInputException;
}
