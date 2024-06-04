package com.bodanka.learnnplay.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/all")
    public String all() {
        return "hi all";
    }

    @GetMapping("/authenticated")
    public String authenticated(Authentication authentication) {
        return "hi " + authentication.getName();
    }
}
