package com.bodanka.learnnplay.controller;

import com.bodanka.learnnplay.domain.Grade;
import com.bodanka.learnnplay.domain.dto.response.ResponseClassDto;
import com.bodanka.learnnplay.domain.dto.response.ResponseClassSectionDto;
import com.bodanka.learnnplay.domain.dto.response.ResponseTestDto;
import com.bodanka.learnnplay.domain.entity.Class;
import com.bodanka.learnnplay.domain.entity.Test;
import com.bodanka.learnnplay.domain.entity.Theme;
import com.bodanka.learnnplay.exception.BadRequestException;
import com.bodanka.learnnplay.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/classes")
public class ClassController {
    private final ClassService classService;

    @GetMapping("/{grade}")
    public ResponseEntity<ResponseClassDto> getClass(@PathVariable(name = "grade") int gradeValue) {
        Grade grade = Grade.fromGradeValue(gradeValue);
        Optional<Class> classByGrade = classService.findByGrade(grade);
        if (classByGrade.isEmpty()) {
            throw new BadRequestException("Class " + gradeValue + " not found");
        }

        Class clazz = classByGrade.get();

        List<ResponseClassSectionDto> themes = clazz.getThemes().stream()
                .sorted(Comparator.comparing(Theme::getCreatedAt).reversed())
                .map(theme -> {
                    List<ResponseTestDto> tests = theme.getTests().stream()
                            .sorted(Comparator.comparing(Test::getCreatedAt).reversed())
                            .map(test -> new ResponseTestDto(test.getId(), test.getTitle()))
                            .toList();
                    return new ResponseClassSectionDto(theme.getId(), theme.getTitle(), tests);
                })
                .toList();

        return ResponseEntity.ok(new ResponseClassDto(clazz.getId(), grade.getGradeValue(), themes));
    }

    @GetMapping
    public ResponseEntity<Map<Integer, String>> getClasses() {
        Map<Integer, String> integerClassMap = Arrays.stream(Grade.values())
                .collect(Collectors.toMap(Grade::getGradeValue, grade -> classService.findByGrade(grade).get().getId()));
        return ResponseEntity.ok(integerClassMap);
    }
}
