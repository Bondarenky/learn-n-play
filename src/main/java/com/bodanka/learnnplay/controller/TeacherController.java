package com.bodanka.learnnplay.controller;

import com.bodanka.learnnplay.domain.Role;
import com.bodanka.learnnplay.domain.dto.request.RequestUserDto;
import com.bodanka.learnnplay.domain.dto.response.ResponseUserDto;
import com.bodanka.learnnplay.domain.entity.User;
import com.bodanka.learnnplay.domain.mapper.Mapper;
import com.bodanka.learnnplay.service.AuthService;
import com.bodanka.learnnplay.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@PreAuthorize("hasRole('TEACHER')")
@RestController
@RequiredArgsConstructor
@RequestMapping("/teachers")
public class TeacherController {
    private final AuthService authService;
    private final UserService userService;
    private final Mapper<User, RequestUserDto, ResponseUserDto> userMapper;

    @PostMapping("/students")
    public ResponseEntity<ResponseUserDto> createStudent(@RequestBody RequestUserDto dto, Authentication authentication) {
        User teacher = userService.findByEmail(authentication.getName()).orElseThrow(RuntimeException::new);

        if (teacher.getRole() != Role.TEACHER) {
            throw new RuntimeException("Teacher is not teacher");
        }

        User student = authService.signUp(userMapper.toEntity(dto));

        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("Student is not student");
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
