package com.demo.app.controller;

import com.demo.app.dto.message.ResponseMessage;
import com.demo.app.dto.student.StudentRequest;
import com.demo.app.dto.student.StudentUpdateRequest;
import com.demo.app.exception.FileInputException;
import com.demo.app.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/student")
@Tag(name = "Student", description = "Student APIs Management")
@AllArgsConstructor
@CrossOrigin(allowedHeaders = "*", origins = "*")
public class StudentController {
    private final String EXAMPLE_LIST_STUDENT_RESPONSES = """
            [
                {
                    "id": 1,
                    "username":"NguyenVanA00",
                    "fullName":"Alice Boudering",
                    "birthday":"2002-08-03",
                    "phoneNumber":"0987654654",
                    "email":"alicee@gmail.com",
                    "code":"20201234",
                    "gender":"FEMALE",
                    "course" : 65
                },
                {
                    "id":3,
                    "username":"NguyenVanB01",
                    "fullName":"Nguyen Van B",
                    "birthday":"2000-07-08",
                    "phoneNumber":"0987654312",
                    "email":"vanb00@gmail.com",
                    "code":"20181234",
                    "gender":"MALE",
                    "course" : 63
                }
            ]
            """;
    private final String EXAMPLE_STUDENT_INFORMATION_CREATE = """
            {
                "username" : "NguyenVanA00",
                "password" : "vana123",
                "email":"nguyenvana123@gmail.com",
                "fullName":"Nguyen Van A",
                "course":"65",
                "birthday":"2002-01-01",
                "gender":"MALE",
                "phoneNumber":"0987654321",
                "code":"20201234",
                "course" : 65
            }
            """;
    private final String EXAMPLE_STUDENT_INFORMATION_UPDATE = """
            {
                "email":"nguyenvana123@gmail.com",
                "fullName":"Nguyen Van A",
                "course":"65",
                "birthday":"2002-01-01",
                "gender":"MALE",
                "phoneNumber":"0987654321",
                "code":"20201234",
                "course" : 65
            }
            """;
    private final String EXAMPLE_NO_DATA_IN_DB = """
            {
                "message" : "no information in database"
            }
            """;
    private final String EXAMPLE_INFORMATION_NOT_FOUND = """
            {
                "message" : "information not found"
            }
            """;
    private final String EXAMPLE_UPDATE_SUCCESS = """
            {
                "message" : "Student with id = %d updated successfully !"
            }
            """;
    private final StudentService studentService;

    @PostMapping(path = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importExcelFile(@RequestPart final MultipartFile file) throws FileInputException {
        studentService.saveStudentsExcelFile(file);
        String message = "Uploaded the file successfully: " + file.getOriginalFilename();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage(message));
    }

    @GetMapping(path = "/export")
    public ResponseEntity<?> exportExcelFile() throws IOException {
        String filename = "Students" + System.currentTimeMillis() + ".xlsx";
        var file = new InputStreamResource(studentService.exportStudentsExcel());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @Operation(
            summary = "Register a new Student",
            description = "Register a new Student by form submit",
            method = "POST",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudentRequest.class, description = "Information's need to be sent to create s new student"),
                            examples = @ExampleObject(value = EXAMPLE_STUDENT_INFORMATION_CREATE)),
                    required = true),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "New Student is created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class, description = "Create a new student"),
                                    examples = @ExampleObject(value = "New student is created"))),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Information is duplicated")
            })
    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addNewStudent(@RequestBody @Valid StudentRequest request) {
        studentService.saveStudent(request);
        String message = String.format("Student %s have been saved successfully !", request.getFullName());
        return new ResponseEntity<>(new ResponseMessage(message), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Return all students",
            description = "return all list of all information of students",
            method = "GET",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Return all information of students successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = List.class, description = "Return a list"),
                                    examples = @ExampleObject(description = "Return a list", value = EXAMPLE_LIST_STUDENT_RESPONSES))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "There is no student in database",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class, description = "Information is incorrect"),
                                    examples = @ExampleObject(description = "Information is incorrect", value = EXAMPLE_NO_DATA_IN_DB))),
                    @ApiResponse(responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    schema = @Schema(implementation = String.class),
                                    examples = @ExampleObject(description = "Unauthorized", value = "Error: Unauthorized")
                            )
                    )
            })
    @GetMapping(path = "/list")
    public ResponseEntity<?> getAllStudents() {
        var studentResponses = studentService.getAllStudents();
        return ResponseEntity.status(HttpStatus.OK).body(studentResponses);
    }

    @Operation(
            summary = "Update Information For A Student",
            description = "Update information student by id and information are needed to updated in request body",
            method = "PUT",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Information student update",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudentRequest.class, description = "Information student update"),
                            examples = @ExampleObject(value = EXAMPLE_STUDENT_INFORMATION_UPDATE)), required = true),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class),
                                    examples = @ExampleObject(
                                            description = "there is string announcement is returned to know that the student is updated",
                                            value = EXAMPLE_UPDATE_SUCCESS))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Information not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class),
                                    examples = @ExampleObject(description = "Information is incorrect", value = EXAMPLE_INFORMATION_NOT_FOUND))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = String.class),
                                    examples = @ExampleObject(description = "Unauthorized", value = "Error: Unauthorized"))
                    )
            })
    @PutMapping(path = "/update/{id}")
    public ResponseEntity<?> updateStudent(
            @Parameter(name = "id", description = "This is the ID student need to be updated", example = "1") @PathVariable(name = "id") int studentId,
            @RequestBody @Valid StudentUpdateRequest request) {
        studentService.updateStudent(studentId, request);
        String message = String.format("Student with id = %d updated successfully !", studentId);
        return new ResponseEntity<>(new ResponseMessage(message), HttpStatus.OK);
    }

    @Operation(
            summary = "Disable student",
            description = "Soft delete student in Database",
            method = "DELETE",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Delete successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Information not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class),
                                    examples = @ExampleObject(
                                            description = "Information is incorrect",
                                            value = EXAMPLE_INFORMATION_NOT_FOUND))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    schema = @Schema(implementation = String.class),
                                    examples = @ExampleObject(description = "Unauthorized", value = "Error: Unauthorized")
                            )
                    )
            })
    @DeleteMapping(path = "/disable/{id}")
    public ResponseEntity<?> disableStudent(
            @Parameter(description = "This is ID of student need to be deleted", example = "1") @PathVariable(name = "id") int studentId){
        studentService.disableStudent(studentId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(new ResponseMessage(String.format("Student with id %d disabled successfully !", studentId)));
    }
}
