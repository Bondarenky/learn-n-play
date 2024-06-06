package com.bodanka.learnnplay.controller;

import com.bodanka.learnnplay.domain.Grade;
import com.bodanka.learnnplay.domain.Role;
import com.bodanka.learnnplay.domain.dto.request.RequestQuestionDto;
import com.bodanka.learnnplay.domain.dto.request.RequestTestAnswersDto;
import com.bodanka.learnnplay.domain.dto.response.ResponseClassWithGradesDto;
import com.bodanka.learnnplay.domain.dto.response.ResponseGradeDto;
import com.bodanka.learnnplay.domain.dto.response.ResponseThemeWithGradeDto;
import com.bodanka.learnnplay.domain.dto.response.ResponseUserWithGradesDto;
import com.bodanka.learnnplay.domain.entity.Test;
import com.bodanka.learnnplay.domain.entity.Theme;
import com.bodanka.learnnplay.domain.entity.User;
import com.bodanka.learnnplay.domain.entity.UserTestGrade;
import com.bodanka.learnnplay.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/grades")
public class UserTestGradeController {
    private final UserTestGradeService userTestGradeService;
    private final TestService testService;
    private final UserService userService;
    private final ClassService classService;
    private final ThemeService themeService;

    @PostMapping
    private ResponseEntity<ResponseGradeDto> create(@RequestBody RequestTestAnswersDto dto, Authentication authentication) {
        try {
            User user = userService.findByEmail(authentication.getName()).orElseThrow(
                    () -> new RuntimeException("User [%s] not found".formatted(authentication.getName()))
            );
            Test test = testService.findById(dto.testId()).orElseThrow(
                    () -> new RuntimeException("Test [%s] not found".formatted(dto.testId()))
            );
            ObjectMapper objectMapper = new ObjectMapper();
            List<RequestQuestionDto> questionsWithCorrectAnswers = objectMapper.readValue(test.getQuestions(), new TypeReference<>() {
            });
            if (dto.questions().size() != questionsWithCorrectAnswers.size()) {
                throw new RuntimeException("You did not answer all the questions");
            }
            Map<String, String> correctAnswersMap = questionsWithCorrectAnswers.stream()
                    .collect(Collectors.toMap(RequestQuestionDto::question, RequestQuestionDto::correctAnswer));
            int res = dto.questions().stream()
                    .map(answer -> correctAnswersMap.get(answer.question()).equals(answer.answer()) ? 1 : 0)
                    .mapToInt(value -> value)
                    .sum();
            double percentage = res / (double) dto.questions().size() * 100;
            userTestGradeService.save(new UserTestGrade(
                    user.getId(),
                    test.getTheme().getClazz().getId(),
                    test.getTheme().getId(),
                    test.getId(),
                    percentage
            ));

            List<UserTestGrade> passedTestsByClass = userTestGradeService.findByUserIdAndClassId(user.getId(), test.getTheme().getClazz().getId());
            double averagePercentage = passedTestsByClass.stream()
                    .mapToDouble(UserTestGrade::getPercentage)
                    .average()
                    .orElse(0);
            long totalTestsByClass = test.getTheme().getClazz().getThemes().stream()
                    .mapToLong(theme -> theme.getTests().size())
                    .sum();

            if (user.getCurrentGrade().getGradeValue() < test.getTheme().getClazz().getGrade().getNext().getGradeValue()) {
                if (passedTestsByClass.size() == totalTestsByClass && averagePercentage >= 50) {
                    userService.increaseGradeByUserId(user.getId(), test.getTheme().getClazz().getGrade().getNext());
                } else {
                    userService.increaseGradeByUserId(user.getId(), test.getTheme().getClazz().getGrade());
                }
            }

            return ResponseEntity.ok(new ResponseGradeDto(res, dto.questions().size(), percentage));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/teachers/students/{email}")
    public ResponseEntity<List<ResponseUserWithGradesDto>> getStudentsGrades(@PathVariable String email) {
        User teacher = userService.findByEmail(email).orElse(User.empty());
        if (teacher.getRole() == null || teacher.getRole() == Role.STUDENT) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        Map<String, List<UserTestGrade>> studentsGrades = teacher.getStudents().stream()
                .flatMap(student -> userTestGradeService.findByUserId(student.getId()).stream())
                .collect(Collectors.groupingBy(UserTestGrade::getUserId, LinkedHashMap::new, Collectors.toList()));

        List<ResponseUserWithGradesDto> usersGrades = studentsGrades.entrySet().stream()
                .map(entry -> {
                    User student = userService.findById(UUID.fromString(entry.getKey())).orElse(User.empty());
                    double totalGrade = entry.getValue().stream()
                            .mapToDouble(UserTestGrade::getPercentage)
                            .average()
                            .orElse(0);

                    Map<String, List<UserTestGrade>> classesGrades = entry.getValue().stream()
                            .collect(Collectors.groupingBy(UserTestGrade::getClassId));

                    List<ResponseClassWithGradesDto> classesWithGrades = classesGrades.entrySet().stream()
                            .map(classEntry -> {
                                Grade grade = classService.findGradeByClassId(classEntry.getKey())
                                        .orElseThrow(() -> new RuntimeException("Class [%s] not found".formatted(classEntry.getKey())));
                                double classGrade = classEntry.getValue().stream()
                                        .mapToDouble(UserTestGrade::getPercentage)
                                        .average()
                                        .orElse(0);

                                Map<String, List<UserTestGrade>> themesGrades = classEntry.getValue().stream()
                                        .collect(Collectors.groupingBy(UserTestGrade::getThemeId));

                                List<ResponseThemeWithGradeDto> themesWithGrades = themesGrades.entrySet().stream()
                                        .map(themeEntry -> {
                                            Theme theme = themeService.findThemeById(themeEntry.getKey())
                                                    .orElseThrow(() -> new RuntimeException("Theme [%s] not found".formatted(themeEntry.getKey())));
                                            double themeGrade = themeEntry.getValue().stream()
                                                    .mapToDouble(UserTestGrade::getPercentage)
                                                    .average()
                                                    .orElse(0);
                                            return new ResponseThemeWithGradeDto(theme.getId(), theme.getTitle(), themeGrade);
                                        })
                                        .sorted(Comparator.comparing(ResponseThemeWithGradeDto::themeTitle))
                                        .toList();

                                return new ResponseClassWithGradesDto(grade.getGradeValue(), classGrade, themesWithGrades);
                            })
                            .sorted(Comparator.comparing(ResponseClassWithGradesDto::grade))
                            .toList();

                    return new ResponseUserWithGradesDto(student.getId(), student.getFirstName(), student.getLastName(), totalGrade, classesWithGrades);
                })
                .sorted(Comparator.comparing(ResponseUserWithGradesDto::firstName))
                .toList();

        List<ResponseUserWithGradesDto> res = new ArrayList<>(usersGrades);

        teacher.getStudents().stream()
                .filter(student -> !usersGrades.stream().map(ResponseUserWithGradesDto::studentId).toList().contains(student.getId()))
                .forEach(student -> res.add(new ResponseUserWithGradesDto(student.getId(), student.getFirstName(), student.getLastName(), null, Collections.emptyList())));

        res.sort(Comparator.comparing(ResponseUserWithGradesDto::firstName));
        return ResponseEntity.ok(res);
    }
}
