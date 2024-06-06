package com.bodanka.learnnplay.controller;

import com.bodanka.learnnplay.domain.Grade;
import com.bodanka.learnnplay.domain.Role;
import com.bodanka.learnnplay.domain.dto.response.ResponseClassDto;
import com.bodanka.learnnplay.domain.dto.response.ResponseClassSectionDto;
import com.bodanka.learnnplay.domain.dto.response.ResponseTestDto;
import com.bodanka.learnnplay.domain.entity.Class;
import com.bodanka.learnnplay.domain.entity.*;
import com.bodanka.learnnplay.exception.BadRequestException;
import com.bodanka.learnnplay.service.ClassService;
import com.bodanka.learnnplay.service.UserService;
import com.bodanka.learnnplay.service.UserTestGradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/classes")
public class ClassController {
    private final ClassService classService;
    private final UserService userService;
    private final UserTestGradeService userTestGradeService;

    @GetMapping("/{grade}")
    public ResponseEntity<ResponseClassDto> getClass(@PathVariable(name = "grade") int gradeValue, Authentication authentication) {
        Class clazz = classService.findByGrade(Grade.fromGradeValue(gradeValue)).orElseThrow(
                () -> new BadRequestException("Class " + gradeValue + " not found")
        );
        User user = userService.findByEmail(authentication.getName()).orElseThrow(
                () -> new RuntimeException("User " + authentication.getName() + " not found")
        );

        String teacherId = user.getRole() == Role.TEACHER ? user.getId() : user.getTeacherId();
        String studentId = user.getRole() == Role.TEACHER ? null : user.getId();
        List<ResponseClassSectionDto> themes = clazz.getThemes().stream()
                .filter(theme -> theme.getUser().getId().equals(teacherId))
                .sorted(Comparator.comparing(Theme::getCreatedAt))
                .map(theme -> {
                    List<ResponseTestDto> tests = theme.getTests().stream()
                            .sorted(Comparator.comparing(Test::getCreatedAt))
                            .map(test -> {
                                Double percentage = null;
                                if (studentId != null) {
                                    percentage = userTestGradeService.findByUserIdAndTestId(studentId, test.getId()).map(UserTestGrade::getPercentage).orElse(null);
                                }
                                return new ResponseTestDto(test.getId(), test.getTitle(), percentage);
                            })
                            .toList();

                    AtomicReference<Double> percentage = new AtomicReference<>();
                    if (studentId != null) {
                        userTestGradeService.findByUserIdAndThemeId(studentId, theme.getId()).stream()
                                .mapToDouble(UserTestGrade::getPercentage)
                                .average()
                                .ifPresent(percentage::set);
                    }

                    return new ResponseClassSectionDto(theme.getId(), theme.getTitle(), percentage.get(), tests);
                })
                .toList();


        AtomicReference<Double> percentage = new AtomicReference<>();
        if (studentId != null) {
            userTestGradeService.findByUserIdAndClassId(studentId, clazz.getId()).stream()
                    .mapToDouble(UserTestGrade::getPercentage)
                    .average()
                    .ifPresent(percentage::set);
        }

        return ResponseEntity.ok(new ResponseClassDto(clazz.getId(), gradeValue, percentage.get(), themes));
    }

    @GetMapping
    public ResponseEntity<Map<Integer, String>> getClasses() {
        Map<Integer, String> integerClassMap = Arrays.stream(Grade.values())
                .collect(Collectors.toMap(Grade::getGradeValue, grade -> classService.findByGrade(grade).get().getId()));
        return ResponseEntity.ok(integerClassMap);
    }
}
