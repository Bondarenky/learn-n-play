package com.bodanka.learnnplay.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @Operation(summary = "Test endpoint to check if security is working. Returns 'hi all' for all users")
    @GetMapping("/all")
    public String all() {
        return "hi all";
    }

    @Operation(summary = "Test endpoint to check if security is working. Returns 'hi' and name of the logged in user")
    @GetMapping("/authenticated")
    public String authenticated(Authentication authentication) {
        return "hi " + authentication.getName();
    }
}
