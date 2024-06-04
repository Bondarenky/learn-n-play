package com.bodanka.learnnplay.controller;

import com.bodanka.learnnplay.domain.dto.request.RequestUserByEmailDto;
import com.bodanka.learnnplay.domain.dto.request.RequestUserDto;
import com.bodanka.learnnplay.domain.dto.response.ResponseCurrentUserDto;
import com.bodanka.learnnplay.domain.dto.response.ResponseUserDto;
import com.bodanka.learnnplay.domain.entity.User;
import com.bodanka.learnnplay.domain.mapper.Mapper;
import com.bodanka.learnnplay.exception.BadRequestException;
import com.bodanka.learnnplay.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final Mapper<User, RequestUserDto, ResponseUserDto> userMapper;

    @PostMapping
    public ResponseEntity<ResponseUserDto> createUser(@RequestBody RequestUserDto dto) {
        User saved = userService.save(userMapper.toEntity(dto));
        return new ResponseEntity<>(userMapper.toResponseDto(saved), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseUserDto> getUserById(@PathVariable UUID id) {
        Optional<User> byId = userService.findById(id);
        if (byId.isEmpty()) {
            throw new BadRequestException("User with id [%s] not found".formatted(id));
        }
        return ResponseEntity.ok(userMapper.toResponseDto(byId.get()));
    }

    @PostMapping("/email")
    public ResponseEntity<ResponseUserDto> getUserByEmail(@RequestBody RequestUserByEmailDto dto) {
        Optional<User> byEmail = userService.findByEmail(dto.email());
        if (byEmail.isEmpty()) {
            throw new BadRequestException("User with email [%s] not found".formatted(dto.email()));
        }
        return ResponseEntity.ok(userMapper.toResponseDto(byEmail.get()));
    }

    @GetMapping
    public ResponseEntity<List<ResponseUserDto>> getAllUsers() {
        List<ResponseUserDto> list = userService.findAll().stream()
                .map(userMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/current")
    public ResponseEntity<ResponseCurrentUserDto> getCurrentUser(Authentication authentication) {
        User current = userService.findByEmail(authentication.getName()).orElseThrow(
                () -> new BadRequestException("User with email [%s] not found".formatted(authentication.getName()))
        );
        return ResponseEntity.ok(new ResponseCurrentUserDto(
                current.getFirstName(),
                current.getLastName(),
                current.getEmail(),
                current.getRole().name(),
                current.getCurrentGrade().getGradeValue()
        ));
    }
}
