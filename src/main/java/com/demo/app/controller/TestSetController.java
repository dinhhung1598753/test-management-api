package com.demo.app.controller;

import com.demo.app.dto.message.ResponseMessage;
import com.demo.app.service.TestSetService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
                                                   @RequestParam final Integer testSetQuantity) throws InterruptedException {
        testSetService.createTestSetFromTest(testId, testSetQuantity);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseMessage("Created Set of test successfully !"));
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

    @GetMapping(path = "/detail/{test-id}/{test-no}")
    public ResponseEntity<?> getTestSetDetail(@PathVariable(name = "test-id") Integer testId,
                                              @PathVariable(name = "test-no") Integer testNo) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(testSetService.getTestSetDetail(testId, testNo));
    }
}
