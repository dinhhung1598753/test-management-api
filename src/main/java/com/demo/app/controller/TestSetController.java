package com.demo.app.controller;

import com.demo.app.dto.message.ResponseMessage;
import com.demo.app.dto.testset.TestSetRequest;
import com.demo.app.service.TestSetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping(path = "/api/v1/test-set")
@Tag(name = "Test-Set", description = "TestSet API management")
@RequiredArgsConstructor
public class TestSetController {

    private final TestSetService testSetService;

    @PostMapping(path = "/{test-id}/create")
    public ResponseEntity<?> createTestSetFromTest(@PathVariable(name = "test-id") int testId,
                                                   @RequestBody @Valid final TestSetRequest request) throws InterruptedException {
        testSetService.createTestSetFromTest(testId, request);
        Thread.sleep(5000);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseMessage("Created Set of test successfully !"));
    }

    @GetMapping(path = "/list")
    public ResponseEntity<?> getAllTestSet() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(testSetService.getAllTestSet());
    }

    @GetMapping(path = "/word/export/{id}")
    public ResponseEntity<?> downloadTestSetWordFile(@PathVariable(name = "id") Integer testSetId) throws IOException {
        var resource = new InputStreamResource(testSetService.exportTestSetToWord(testSetId));
        var filename = "Test" + LocalDate.now() + ".docx";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition
                .attachment()
                .filename(filename)
                .build());
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @GetMapping(path = "/detail/{id}")
    public ResponseEntity<?> getTestSetDetail(@PathVariable(name = "id") int testSetId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(testSetService.getTestSetDetail(testSetId));
    }
}
