package com.demo.app.controller;

import com.demo.app.dto.message.ResponseMessage;
import com.demo.app.exception.InvalidVerificationTokenException;
import com.demo.app.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.spi.ErrorMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class EmailController {
    private final AuthService authService;

    @GetMapping(path = "/verify-email")
    public String verifyEmail(@RequestParam(name = "token") String verifyToken, ModelMap model) {
        try {
            authService.activateUserAccount(verifyToken);
            model.addAttribute("message", new ResponseMessage("Account Activation completed !"));
        }
        catch(InvalidVerificationTokenException ex){
            model.addAttribute("message", new ErrorMessage(ex.getMessage()));
        }
        return "activate-email";
    }
}
