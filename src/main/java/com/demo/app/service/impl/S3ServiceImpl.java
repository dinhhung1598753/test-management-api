package com.demo.app.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.demo.app.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 s3Client;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Override
    public String uploadFile(String keyName, MultipartFile file) throws IOException {
        var metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        s3Client.putObject(bucketName, keyName, file.getInputStream(), metadata);
        return s3Client.getUrl(bucketName, keyName).toString();
    }

    @Override
    public byte[] downloadFile(String key) {
        return null;
    }

}
