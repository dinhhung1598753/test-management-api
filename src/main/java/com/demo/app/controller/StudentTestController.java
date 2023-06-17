package com.demo.app.controller;

import com.demo.app.dto.message.ResponseMessage;
import com.demo.app.dto.student_test.Filename;
import com.demo.app.dto.student_test.TestImageResponse;
import com.demo.app.exception.InvalidRoleException;
import com.demo.app.service.FileStorageService;
import com.demo.app.service.StudentTestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/student-test")
@RequiredArgsConstructor
@Tag(name = "Student-Test", description = "Manage Student's Test and Marking")
public class StudentTestController {

    private final StudentTestService studentTestService;

    private final FileStorageService fileStorageService;

    @PostMapping(path = "/uploads",
            consumes = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<?> uploadStudentTestImages(@RequestParam(name = "exam-class") String classCode,
                                                     @RequestPart(name = "files") List<MultipartFile> files) throws IOException {
        fileStorageService.checkIfFileIsImageFormat(files);
        var path = fileStorageService.createClassDirectory(classCode);
        var imageFilenames = new ArrayList<Filename>();
        for (var file : files) {
            var filename = new Filename();
            filename.setFilename(fileStorageService.upload(classCode, file));
            imageFilenames.add(filename);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(TestImageResponse.builder()
                        .responseMessage(new ResponseMessage("All files uploaded successfully !"))
                        .path(path)
                        .imageFilenames(imageFilenames)
                        .build());
    }


    @GetMapping(path = "/testing")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<?> getRandomTestForStudent(@RequestParam String classCode, Principal principal){
        if (principal == null){
            throw new InvalidRoleException("You're not logged in !", HttpStatus.UNAUTHORIZED);
        }
        studentTestService.matchRandomTestForStudent(classCode, principal);
        return null;
    }

    @PostMapping(path = "/marking")
    public ResponseEntity<?> markingStudentTest() throws IOException {
        studentTestService.markingOfflineAnswer();
        return null;
    }
}
