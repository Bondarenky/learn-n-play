package com.bodanka.learnnplay.controller;

import com.bodanka.learnnplay.domain.Grade;
import com.bodanka.learnnplay.domain.Role;
import com.bodanka.learnnplay.domain.dto.request.RequestThemeDto;
import com.bodanka.learnnplay.domain.dto.response.ResponseThemeDto;
import com.bodanka.learnnplay.domain.dto.response.ResponseThemeWithPercentageDto;
import com.bodanka.learnnplay.domain.entity.Class;
import com.bodanka.learnnplay.domain.entity.Theme;
import com.bodanka.learnnplay.domain.entity.User;
import com.bodanka.learnnplay.domain.entity.UserTestGrade;
import com.bodanka.learnnplay.service.ClassService;
import com.bodanka.learnnplay.service.ThemeService;
import com.bodanka.learnnplay.service.UserService;
import com.bodanka.learnnplay.service.UserTestGradeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/themes")
public class ThemeController {
    private final ThemeService themeService;
    private final UserService userService;
    private final ClassService classService;
    private final UserTestGradeService userTestGradeService;

    @Operation(summary = "Create a theme within specific class")
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

    @Operation(summary = "Update a theme by theme id")
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

    @Operation(summary = "Find all user created themes")
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

    @Operation(summary = "Delete a theme by its id")
    @DeleteMapping("/{themeId}")
    public ResponseEntity<String> deleteTheme(@PathVariable String themeId) {
        return ResponseEntity.ok(themeService.deleteThemeById(themeId));
    }

    @Operation(summary = "Get theme with completion percentage by author's email")
    @GetMapping("/{email}")
    public ResponseEntity<List<ResponseThemeWithPercentageDto>> getThemeByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User [%s] not found".formatted(email)));

        if (user.getRole() == Role.TEACHER) {
            List<ResponseThemeWithPercentageDto> teacherThemes = themeService.findByUser(user).stream()
                    .map(theme -> new ResponseThemeWithPercentageDto(theme.getId(), theme.getTitle(), null))
                    .sorted(Comparator.comparing(ResponseThemeWithPercentageDto::title))
                    .toList();
            return ResponseEntity.ok(teacherThemes);
        }

        Map<String, List<UserTestGrade>> map = userTestGradeService.findByUserId(user.getId()).stream()
                .collect(Collectors.groupingBy(UserTestGrade::getThemeId));
        List<ResponseThemeWithPercentageDto> themesWithPercentage = map.entrySet().stream()
                .map(entry -> new ResponseThemeWithPercentageDto(entry.getKey(), getThemeTitleOrEmpty(entry.getKey()), calculateThemePercentage(entry.getValue())))
                .sorted(Comparator.comparing(ResponseThemeWithPercentageDto::title))
                .toList();
        return ResponseEntity.ok(themesWithPercentage);
    }

    private String getThemeTitleOrEmpty(String themeId) {
        return themeService.findThemeById(themeId).map(Theme::getTitle).orElse("");
    }

    private double calculateThemePercentage(List<UserTestGrade> grades) {
        return grades.stream()
                .mapToDouble(UserTestGrade::getPercentage)
                .average()
                .orElse(0);
    }
}
