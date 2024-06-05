package com.bodanka.learnnplay.controller;

import com.bodanka.learnnplay.domain.dto.response.ResponseCurrentUserDto;
import com.bodanka.learnnplay.domain.entity.User;
import com.bodanka.learnnplay.exception.BadRequestException;
import com.bodanka.learnnplay.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/current")
    public ResponseEntity<ResponseCurrentUserDto> getCurrentUser(Authentication authentication) {
        User current = userService.findByEmail(authentication.getName()).orElseThrow(
                () -> new BadRequestException("User with email [%s] not found".formatted(authentication.getName()))
        );
        User optionalTeacher = User.empty();
        if (StringUtils.hasText(current.getTeacherId())) {
            optionalTeacher = userService.findById(UUID.fromString(current.getTeacherId())).orElse(User.empty());
        }
        return ResponseEntity.ok(new ResponseCurrentUserDto(
                current.getFirstName(),
                current.getLastName(),
                current.getEmail(),
                current.getRole().name(),
                current.getCurrentGrade().getGradeValue(),
                optionalTeacher.getFirstName(),
                optionalTeacher.getLastName(),
                optionalTeacher.getEmail()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.deleteById(id));
    }
}
