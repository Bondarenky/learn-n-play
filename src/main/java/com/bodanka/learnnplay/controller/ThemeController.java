package com.bodanka.learnnplay.controller;

import com.bodanka.learnnplay.domain.Grade;
import com.bodanka.learnnplay.domain.dto.request.RequestThemeDto;
import com.bodanka.learnnplay.domain.dto.response.ResponseThemeDto;
import com.bodanka.learnnplay.domain.entity.Class;
import com.bodanka.learnnplay.domain.entity.Theme;
import com.bodanka.learnnplay.domain.entity.User;
import com.bodanka.learnnplay.service.ClassService;
import com.bodanka.learnnplay.service.ThemeService;
import com.bodanka.learnnplay.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasRole('TEACHER')")
@RestController
@RequiredArgsConstructor
@RequestMapping("/themes")
public class ThemeController {
    private final ThemeService themeService;
    private final UserService userService;
    private final ClassService classService;

    @PostMapping("/{grade}")
    public ResponseEntity<ResponseThemeDto> createTheme(@PathVariable int grade, @RequestBody RequestThemeDto dto, Authentication authentication) {
        User teacher = userService.findByEmail(authentication.getName()).orElseThrow(
                () -> new RuntimeException("Teacher [%s] not found".formatted(authentication.getName()))
        );
        Class clazz = classService.findByGrade(Grade.fromGradeValue(grade)).orElseThrow(
                () -> new RuntimeException("Class [%s] not found".formatted(grade))
        );
        Theme theme = themeService.saveTheme(new Theme(dto.title(), teacher, clazz));
        return new ResponseEntity<>(new ResponseThemeDto(theme.getId(), theme.getTitle()), HttpStatus.CREATED);
    }

    @PutMapping("/{themeId}")
    public ResponseEntity<String> updateTheme(@PathVariable String themeId, @RequestBody RequestThemeDto dto) {
        StringBuilder oldTitle = new StringBuilder();
        themeService.findThemeById(themeId).ifPresent(theme -> {
            oldTitle.append(theme.getTitle());
            theme.setTitle(dto.title());
            themeService.saveTheme(theme);
        });
        return ResponseEntity.ok("Theme [%s] renamed to [%s]".formatted(oldTitle, dto.title()));
    }

    @GetMapping
    public ResponseEntity<List<ResponseThemeDto>> getTheme(Authentication authentication) {
        User teacher = userService.findByEmail(authentication.getName()).orElseThrow(
                () -> new RuntimeException("Teacher [%s] not found".formatted(authentication.getName()))
        );
        List<ResponseThemeDto> themes = themeService.findByUser(teacher).stream()
                .map(theme -> new ResponseThemeDto(theme.getId(), theme.getTitle()))
                .toList();
        return ResponseEntity.ok(themes);
    }
}
