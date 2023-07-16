package com.demo.app.controller;

import com.demo.app.dto.message.ResponseMessage;
import com.demo.app.dto.studentTest.StudentTestFinishRequest;
import com.demo.app.dto.studentTest.TestImageResponse;
import com.demo.app.exception.FileInputException;
import com.demo.app.exception.UserNotSignInException;
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
import java.util.List;
import java.util.stream.Collectors;

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
                                                     @RequestPart(name = "files") List<MultipartFile> files) throws FileInputException, IOException {
        fileStorageService.checkIfFileIsImageFormat(files);
        var path = fileStorageService.createClassDirectory("answer_sheets/" + classCode);
        var imageFilenames = files.stream()
                .map(file -> TestImageResponse.Filename.builder()
                        .filename(fileStorageService.upload(path, file))
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK)
                .body(TestImageResponse.builder()
                        .responseMessage(new ResponseMessage("All files uploaded successfully !"))
                        .path(path)
                        .imageFilenames(imageFilenames)
                        .build());
    }

    @GetMapping(path = "/attempt")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<?> attemptTest(@RequestParam String classCode, Principal principal) {
        if (principal == null){
            throw new UserNotSignInException("You are not logged in !", HttpStatus.UNAUTHORIZED);
        }
        var response = studentTestService.attemptTest(classCode, principal);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping(path = "/finish")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<?> finishTest(@RequestBody StudentTestFinishRequest request,
                                        Principal principal) throws InterruptedException {

        studentTestService.finishStudentTest(request, principal);
        return null;
    }

    @PostMapping(path = "/marking")
    public ResponseEntity<?> markingStudentTest() throws IOException {
        studentTestService.markingOfflineAnswer();
        return null;
    }
}
