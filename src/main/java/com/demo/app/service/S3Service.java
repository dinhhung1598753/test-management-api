package com.demo.app.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {

    String uploadFile(String keyName, MultipartFile file) throws IOException;

    byte[] downloadFile(String key);
}
