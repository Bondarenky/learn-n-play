package com.bodanka.learnnplay.controller;

import com.bodanka.learnnplay.domain.Grade;
import com.bodanka.learnnplay.domain.Role;
import com.bodanka.learnnplay.domain.dto.request.SignInRequestDto;
import com.bodanka.learnnplay.domain.dto.request.SignUpRequestDto;
import com.bodanka.learnnplay.domain.dto.response.SignInResponseDto;
import com.bodanka.learnnplay.domain.entity.User;
import com.bodanka.learnnplay.exception.BadRequestException;
import com.bodanka.learnnplay.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Register user in the system and send an email to verify an account")
    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequestDto dto) {
        if (!Objects.equals(dto.password(), dto.confirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
        User user = authService.signUp(new User(dto.firstName(), dto.lastName(), dto.email(), dto.password(), Role.TEACHER, Grade.ELEVEN));
        return ResponseEntity.ok("Sign up successful. Go to " + user.getEmail() + " to activate an account.");
    }

    @Operation(summary = "Verify the token that was sent in the sign-up request")
    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam("token") String token) {
        if (authService.verifyEmailVerificationToken(token)) {
            return ResponseEntity.ok("User is verified. You can close this tab.");
        }

        return new ResponseEntity<>("Invalid email verification link.", HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Generate and return an access token for that user if the credentials are valid")
    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponseDto> signIn(@RequestBody SignInRequestDto dto) {
        String accessToken = authService.signIn(dto.email(), dto.password());
        return ResponseEntity.ok(new SignInResponseDto(accessToken));
    }
}
