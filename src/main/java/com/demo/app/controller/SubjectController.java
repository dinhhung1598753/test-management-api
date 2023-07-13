package com.demo.app.controller;

import com.demo.app.dto.chapter.ChapterRequest;
import com.demo.app.dto.message.ResponseMessage;
import com.demo.app.dto.subject.SubjectChaptersRequest;
import com.demo.app.dto.subject.SubjectRequest;
import com.demo.app.service.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/subject")
@RequiredArgsConstructor
@Tag(name = "Subject", description = "Subject API Management")
public class SubjectController {

    private final SubjectService subjectService;

    @Operation(
            description = "Add a new Subject by data sent from client",
            method = "POST",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "This is data sent by client",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SubjectRequest.class, description = "Information need to create a Subject"),
                            examples = @ExampleObject())),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "New Subject is created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class, description = "Create a new subject"),
                                    examples = @ExampleObject())),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Subject code is already taken",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class)))
            })
    @PostMapping(path = "/add")
    public ResponseEntity<?> addSubject(@RequestBody @Valid final SubjectRequest request) {
        subjectService.addSubject(request);
        String message = "Add subject successfully !";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseMessage(message));
    }

    public ResponseEntity<?> addSubjectWithChapters(@RequestBody final SubjectChaptersRequest request) {

        return null;
    }

    @Operation(
            description = "Get all the subjects",
            method = "GET",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Return a list of subject in database",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = List.class, description = "Create a new subject"),
                                    examples = @ExampleObject()))
            })
    @GetMapping(path = "/list")
    public ResponseEntity<?> listSubject() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(subjectService.getAllSubjects());
    }

    @GetMapping(path = "/chapters")
    public ResponseEntity<?> listSubjectWithChapters() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(subjectService.getAllSubjectsWithChapters());
    }

    @GetMapping(path = "/{code}")
    public ResponseEntity<?> subjectWithChapters(@PathVariable String code){
        return ResponseEntity.status(HttpStatus.OK)
                .body(subjectService.getSubjectWithChapter(code));
    }

    @Operation(
            description = "Update subject",
            method = "PUT",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "This is data sent by client",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SubjectRequest.class,
                                    description = "Information need to update a Subject"),
                            examples = @ExampleObject()

                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class,
                                            description = "Updated successfully"),
                                    examples = @ExampleObject())
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Data not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class, description = "Data not found"),
                                    examples = @ExampleObject()
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Data conflict",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class, description = "Data conflict"),
                                    examples = @ExampleObject()
                            )
                    )
            }
    )
    @PutMapping(path = "/update/{id}")
    public ResponseEntity<?> updateSubject(@PathVariable(name = "id") int subjectId, @RequestBody @Valid final SubjectRequest request) {
        subjectService.updateSubject(subjectId, request);
        return new ResponseEntity<>(new ResponseMessage("Update subject successfully !"), HttpStatus.OK);
    }

    @PutMapping(path = "/update/{id}/chapters")
    public ResponseEntity<?> updateSubjectChapters(@PathVariable(name = "id") Integer subjectId){
        return null;
    }

    @Operation(
            description = "Delete subject",
            method = "DELETE",
            responses = {
                    @ApiResponse(
                            responseCode = "404",
                            description = "Data not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class, description = "Data not found"),
                                    examples = @ExampleObject()
                            )
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "No Content"
                    )
            }
    )
    @DeleteMapping(path = "/disable/{id}")
    public ResponseEntity<?> disableSubject(@Parameter @PathVariable(name = "id") int subjectId) {
        subjectService.disableSubject(subjectId);
        return new ResponseEntity<>(new ResponseMessage("Update subject successfully !"), HttpStatus.NO_CONTENT);
    }
    @Operation(
            description = "Delete subject",
            method = "DELETE",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "No Content",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = List.class, description = "Return list of chapter on a subject"),
                                    examples = @ExampleObject()
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Data not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class, description = "Data not found"),
                                    examples = @ExampleObject()
                            )
                    )
            }
    )
    @GetMapping(path = "/{code}/chapter/list")
    public ResponseEntity<?> getAllSubjectChapters( @Parameter @PathVariable(name = "code") String code){
        return ResponseEntity.ok().body(subjectService.getAllSubjectChapters(code));
    }
    @Operation(
            description = "Add a new Chapter by data sent from client",
            method = "POST",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "This is data sent by client",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChapterRequest.class, description = "Information need to create a Chapter"),
                            examples = @ExampleObject()

                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "New Subject is created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class, description = "Create a new Chapter"),
                                    examples = @ExampleObject()
                            )

                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "This chapter is already taken",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Data not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class)
                            )
                    )
            }
    )
    @PostMapping(path = "/{code}/chapter/add")
    public ResponseEntity<?> addSubjectChapter(@Parameter @PathVariable(name = "code") String code, @RequestBody @Valid final ChapterRequest request){
        subjectService.addSubjectChapter(code, request);
        return new ResponseEntity<>(new ResponseMessage("Add subject's chapter successfully !"), HttpStatus.CREATED);
    }
    @Operation(
            description = "Add a new Chapter by data sent from client",
            method = "POST",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "This is data sent by client",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChapterRequest.class, description = "Information need to create a Chapter"),
                            examples = @ExampleObject()

                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "New Subject is created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class, description = "Create a new Chapter"),
                                    examples = @ExampleObject()
                            )

                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "This chapter is already taken",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Data not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class)
                            )
                    )
            }
    )
    @PostMapping(path = "/{code}/chapters/add")
    public ResponseEntity<?> addSubjectChapters(@PathVariable(name = "code") String code, @RequestBody @Valid final List<ChapterRequest> request){
        subjectService.addSubjectChapters(code, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessage("Add subject's chapters successfully !"));
    }

    @Operation(
            description = "Update subject",
            method = "PUT",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "This is data sent by client",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChapterRequest.class, description = "Information need to update a Chapter"),
                            examples = @ExampleObject()

                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class, description = "Updated successfully"),
                                    examples = @ExampleObject()
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Data not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class, description = "Data not found"),
                                    examples = @ExampleObject()
                            )
                    )
            }
    )
    @PutMapping(path = "/chapter/update/{id}")
    public ResponseEntity<?> updateChapter(@Parameter @PathVariable(name = "id") int chapterId, @RequestBody @Valid final ChapterRequest request){
        subjectService.updateSubjectChapter(chapterId, request);
        return new ResponseEntity<>(new ResponseMessage("Update chapter successfully !"), HttpStatus.OK);
    }

    @Operation(
            description = "Delete subject",
            method = "DELETE",
            responses = {
                    @ApiResponse(
                            responseCode = "404",
                            description = "Data not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class, description = "Data not found"),
                                    examples = @ExampleObject()
                            )
                    ),
                    @ApiResponse(
                            responseCode = "200",
                            description = "delete successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class, description = "delete successfully"),
                                    examples = @ExampleObject()
                            )
                    )
            }
    )
    @DeleteMapping(path = "/chapter/disable/{id}")
    public ResponseEntity<?> disableChapter(@Parameter @PathVariable(name = "id") int chapterId){
        subjectService.disableChapter(chapterId);
        return new ResponseEntity<>(new ResponseMessage("Disable chapter successfully !"), HttpStatus.OK);
    }
}
