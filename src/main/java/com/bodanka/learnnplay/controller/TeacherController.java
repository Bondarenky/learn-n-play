package com.bodanka.learnnplay.controller;

import com.bodanka.learnnplay.domain.Role;
import com.bodanka.learnnplay.domain.dto.request.RequestUserDto;
import com.bodanka.learnnplay.domain.dto.request.SignUpRequestDto;
import com.bodanka.learnnplay.domain.dto.response.ResponseUserDto;
import com.bodanka.learnnplay.domain.entity.User;
import com.bodanka.learnnplay.domain.mapper.Mapper;
import com.bodanka.learnnplay.exception.BadRequestException;
import com.bodanka.learnnplay.service.AuthService;
import com.bodanka.learnnplay.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teachers")
public class TeacherController {
    private final AuthService authService;
    private final UserService userService;
    private final Mapper<User, RequestUserDto, ResponseUserDto> userMapper;

    @PostMapping("/students")
    public ResponseEntity<ResponseUserDto> createStudent(@RequestBody SignUpRequestDto dto, Authentication authentication) {
        User teacher = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new BadRequestException("Teacher [%s] not found".formatted(authentication.getName())));

        if (teacher.getRole() != Role.TEACHER) {
            throw new BadRequestException("%s is not a teacher".formatted(teacher.getFirstName() + " " + teacher.getLastName()));
        }

        if (!Objects.equals(dto.password(), dto.confirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        User student = authService.signUp(new User(dto.firstName(), dto.lastName(), dto.email(), dto.password(), Role.STUDENT));

        if (student.getRole() != Role.STUDENT) {
            throw new BadRequestException("%s is not a student".formatted(student.getFirstName() + " " + student.getLastName()));
        }

        userService.addStudent(teacher, student);
        return ResponseEntity.ok(userMapper.toResponseDto(student));
    }

    @GetMapping("/students")
    public ResponseEntity<List<ResponseUserDto>> getStudents(Authentication authentication) {
        List<User> students = new ArrayList<>();
        userService.findByEmail(authentication.getName())
                .ifPresent(teacher -> students.addAll(userService.findByTeacher(UUID.fromString(teacher.getId()))));
        List<ResponseUserDto> studentsDtos = students.stream()
                .map(userMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(studentsDtos);
    }
}
