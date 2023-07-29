package com.demo.app.controller;

import com.demo.app.dto.examClass.ClassRequest;
import com.demo.app.dto.message.ResponseMessage;
import com.demo.app.exception.UserNotSignInException;
import com.demo.app.service.ExamClassService;
import com.demo.app.util.excel.ExcelUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping(path = "/api/v1/class")
@Tag(name = "Exam-Class")
@RequiredArgsConstructor
public class ExamClassController {

    private final ExamClassService examClassService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('TEACHER')")
    public ResponseEntity<?> createExamClass(@RequestBody ClassRequest request, Principal principal) {
        if (principal == null) {
            throw new UserNotSignInException("You're not logged in !", HttpStatus.UNAUTHORIZED);
        }
        examClassService.createExamClass(request, principal);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseMessage("Create Exam Class successfully !"));
    }

    @PostMapping("/join")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<?> joinExamClassByCode(@RequestParam String classCode, Principal principal) {
        if (principal == null || principal.getName().equals("anonymousUser")){
            throw new UserNotSignInException("You're not logged in !", HttpStatus.UNAUTHORIZED);
        }
        var examClass = examClassService.joinExamClassByCode(classCode, principal);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage(String.format("Join class %s successfully !", examClass.getRoomName())));
    }

    @PostMapping(path = "/import/students", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importStudentExamClass(@RequestPart String classCode,
                                                    @RequestPart MultipartFile file) throws IOException {
        examClassService.importClassStudents(classCode, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseMessage("Import Students to class successfully !"));
    }

    @GetMapping(path = "/list")
    public ResponseEntity<?> getAllExamClass() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(examClassService.getAllEnabledExamClass());
    }

    @GetMapping(path = "/student/list")
    public ResponseEntity<?> getStudentExamClass(Principal principal) {
        if (principal == null) {
            throw new UserNotSignInException("You are not logged in !", HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(examClassService.getStudentExamClass(principal));
    }

    @GetMapping(path = "/info/{id}")
    public ResponseEntity<?> getExamClassInfo(@PathVariable(name = "id") Integer examClassId, Principal principal) {
        if (principal == null || principal.getName().equals("anonymousUser")) {
            throw new UserNotSignInException("You are not logged in", HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(examClassService.getExamClassInfo(examClassId, principal));
    }

    @GetMapping(path = "/detail/{id}")
    public ResponseEntity<?> getExamClassDetail(@PathVariable(name = "id") final Integer examClassId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(examClassService.getExamClassDetail(examClassId));
    }

    @GetMapping(path = "/export/{code}")
    public ResponseEntity<?> exportStudentTestExcel(@PathVariable(name = "code") String code) throws IOException {
        var resource = new InputStreamResource(examClassService.exportStudentTestToExcel(code));
        var filename = "StudentTest:" + code + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }

    @DeleteMapping(path = "/disable/{id}")
    public ResponseEntity<?> disableExamClass(@PathVariable(name = "id") int examClassId) {
        examClassService.disableExamClass(examClassId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(new ResponseMessage("Disabled exam class successfully !"));
    }

    @PostMapping(path = "/test",
            consumes = {
                    MediaType.MULTIPART_FORM_DATA_VALUE
                    , MediaType.APPLICATION_JSON_VALUE})
    private void test(@RequestPart() MultipartFile file) throws IOException {
        ExcelUtils.readExternalExcelFile(file);
    }

}
