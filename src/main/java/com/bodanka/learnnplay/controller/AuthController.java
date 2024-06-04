package com.bodanka.learnnplay.controller;

import com.bodanka.learnnplay.domain.Grade;
import com.bodanka.learnnplay.domain.Role;
import com.bodanka.learnnplay.domain.dto.request.SignInRequestDto;
import com.bodanka.learnnplay.domain.dto.request.TeacherSignUpRequestDto;
import com.bodanka.learnnplay.domain.dto.response.SignInResponseDto;
import com.bodanka.learnnplay.domain.entity.User;
import com.bodanka.learnnplay.exception.BadRequestException;
import com.bodanka.learnnplay.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody TeacherSignUpRequestDto dto) {
        if (!dto.password().equals(dto.confirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
        User user = authService.signUp(new User(dto.firstName(), dto.lastName(), dto.email(), dto.password(), Role.TEACHER, Grade.ELEVEN));
        return ResponseEntity.ok("Sign up successful. Go to " + user.getEmail() + " to activate an account.");
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam("token") String token) {
        if (authService.verifyEmailVerificationToken(token)) {
            return ResponseEntity.ok("User is verified. You can close this tab.");
        }

        return new ResponseEntity<>("Invalid email verification link.", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponseDto> signIn(@RequestBody SignInRequestDto dto) {
        String accessToken = authService.signIn(dto.email(), dto.password());
        return ResponseEntity.ok(new SignInResponseDto(accessToken));
    }
}
