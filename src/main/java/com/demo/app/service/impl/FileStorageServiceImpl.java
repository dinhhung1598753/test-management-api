package com.demo.app.service.impl;

import com.demo.app.exception.FileInputException;
import com.demo.app.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-path}")
    private String uploadPath;

    private static final Set<String> FILE_FORMATs = Set.of(new String[] {".jpg", ".jpeg", ".png"});

    @Override
    public String createClassDirectory(String classCode) throws IOException {
        var path = uploadPath + "/" + classCode;
        Path of = Path.of(path);
        if (!Files.exists(of)) {
            Files.createDirectories(of);
        }
        return path;
    }

    @Override
    public String upload(String classCode, MultipartFile file) throws FileInputException {
        var targetPath = uploadPath + "/" + classCode + "/" + file.getOriginalFilename();
        try {
            Files.copy(file.getInputStream(), Path.of(targetPath));
        } catch (IOException e) {
            throw new FileInputException("Could not upload the file, error: " + e, HttpStatus.EXPECTATION_FAILED);
        }
        return file.getOriginalFilename();
    }

    @Override
    public void checkIfFileIsImageFormat(List<MultipartFile> files) throws FileInputException{
        for (var file : files) {
            var filename = file.getOriginalFilename();
            var fileFormat = filename != null ? filename.substring(filename.lastIndexOf(".")) : "";
            if (!FILE_FORMATs.contains(fileFormat)) {
                throw new FileInputException("File format must be .jpg, .jpeg or .png", HttpStatus.BAD_REQUEST);
            }
        }

    }
}
