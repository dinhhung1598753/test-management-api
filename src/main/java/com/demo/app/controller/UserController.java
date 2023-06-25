package com.demo.app.controller;

import com.demo.app.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/user")
@AllArgsConstructor
@Tag(name = "User")
public class UserController {
    private final UserService userService;

    @GetMapping("/list")
    public ResponseEntity<?> getUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers());
    }

    @GetMapping("/profile")
    public ResponseEntity<?> userProfile(){
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var profile = userService.getUserProfile(auth);
        return ResponseEntity.status(HttpStatus.OK).body(profile);
    }

//    @PutMapping(path = "/update")
//    public ResponseEntity<?> updateProfile(@RequestBody final ){
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        return null;
//    }
}
