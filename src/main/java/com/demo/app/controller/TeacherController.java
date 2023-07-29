package com.demo.app.controller;

import com.demo.app.dto.message.ResponseMessage;
import com.demo.app.dto.teacher.TeacherRequest;
import com.demo.app.dto.teacher.TeacherUpdateRequest;
import com.demo.app.exception.DuplicatedUniqueValueException;
import com.demo.app.exception.InvalidRoleException;
import com.demo.app.service.TeacherService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/teacher")
@AllArgsConstructor
@Tag(name = "Teacher")
public class TeacherController {

    private final String EXAMPLE_INFORMATION_NOT_FOUND = """
                {
                    "message" : "information not found"
                }
            """;

    //private final String EXAMPLE_STUDENT_INFORMATION_CREATE_AND_UPDATE = "";

    private final TeacherService teacherService;


    @Operation(
            summary = "Create a new Teacher",
            description = "Create a new teacher by form is sent",
            method = "POST",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "These are group of information used for teacher register",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    description = "This is structure of data teacher register",
                                    implementation = TeacherRequest.class
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            description = "New teacher is created successfully",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ResponseMessage.class
                                    ),
                                    examples = @ExampleObject(
                                            value = "Teacher %s have been saved successfully !"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Information is duplicated"
                    )
            }
    )
    @PostMapping(path = "/add")
    public ResponseEntity<?> addNewTeacher(@RequestBody @Valid TeacherRequest request) throws DuplicatedUniqueValueException {
        teacherService.saveTeacher(request);
        String message = String.format("Teacher %s have been saved successfully !", request.getFullName());
        return new ResponseEntity<>(new ResponseMessage(message), HttpStatus.CREATED);

    }

    @PostMapping(path = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importTeacherExcel(@RequestPart final MultipartFile file) throws IOException {
        teacherService.importTeacherExcel(file);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessage("Uploaded the file successfully: " + file.getOriginalFilename()));
    }

    @GetMapping(path = "/export")
    public ResponseEntity<?> exportTeacherExcel() throws IOException {
        String filename = "Teachers" + LocalDateTime.now() + ".xlsx";
        var resource = new InputStreamResource(teacherService.exportTeachersToExcel());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }

    @Operation(
            summary = "Return all teacher",
            description = "Return all the information of teachers in the database",
            method = "GET",
            responses = {
                    @ApiResponse(
                            description = "Return all information of teachers successfully",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            description = "return a list of teacher",
                                            implementation = List.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            description = "No information in database",
                            responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ResponseMessage.class
                                    ),
                                    examples = @ExampleObject(
                                            value = "{\"message\":\"Data not found\"}"
                                    )
                            )
                    )
            }
    )
    @GetMapping(path = "/list")
    public ResponseEntity<?> getAllTeachers() {
        var teacherResponses = teacherService.getAllTeacher();
        return ResponseEntity.status(HttpStatus.OK).body(teacherResponses);
    }


    @Operation(
            summary = "Update teacher",
            description = "Update information for teacher by information is sent",
            method = "PUT",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = TeacherRequest.class
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ResponseMessage.class
                                    ),
                                    examples = @ExampleObject(
                                            value = "{\"message\":\"Teacher with id = %d updated successfully !\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Information not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ResponseMessage.class),
                                    examples = @ExampleObject(
                                            description = "Information is incorrect",
                                            value = EXAMPLE_INFORMATION_NOT_FOUND)))})
    @PutMapping(path = "/update/{id}")
    public ResponseEntity<?> updateTeacher(@Parameter(
            description = "This is ID of teacher need to be updated",
            example = "1"
    ) @PathVariable("id") int teacherId, @RequestBody @Valid TeacherUpdateRequest request) {
        teacherService.updateTeacherById(teacherId, request);
        @SuppressWarnings("DefaultLocale") var message = String.format("Teacher with id = %d updated successfully !", teacherId);
        return new ResponseEntity<>(new ResponseMessage(message), HttpStatus.OK);
    }

    @PutMapping(path = "/update/profile")
    @PreAuthorize("hasAnyRole('TEACHER')")
    public ResponseEntity<?> updateStudentProfile(@RequestBody @Valid TeacherUpdateRequest request, Principal principal){
        if (principal == null || principal.getName().equalsIgnoreCase("anonymousUser")){
            throw new InvalidRoleException("You're not logged in !", HttpStatus.UNAUTHORIZED);
        }
        teacherService.updateTeacherProfile(principal, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessage("Profile updated successfully !"));
    }

    @Operation(
            summary = "Delete teacher",
            description = "Delete teacher in Database",
            method = "DELETE",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Delete successfully"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Information not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ResponseMessage.class
                                    ),
                                    examples = @ExampleObject(
                                            description = "Information is incorrect",
                                            value = EXAMPLE_INFORMATION_NOT_FOUND))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = String.class),
                                    examples = @ExampleObject(
                                            description = "Unauthorized",
                                            value = "Error: Unauthorized")))}
    )
    @DeleteMapping(path = "/disable/{id}")
    public ResponseEntity<?> disableTeacher(
            @Parameter(description = "Student ID need to be deleted", example = "1")
            @PathVariable("id") int teacherId) {
        teacherService.disableTeacher(teacherId);
        return ResponseEntity.noContent()
                .build();
    }

}
