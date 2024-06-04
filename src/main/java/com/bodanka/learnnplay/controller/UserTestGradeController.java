package com.bodanka.learnnplay.controller;

import com.bodanka.learnnplay.domain.dto.request.RequestQuestionDto;
import com.bodanka.learnnplay.domain.dto.request.RequestTestAnswersDto;
import com.bodanka.learnnplay.domain.dto.response.ResponseGradeDto;
import com.bodanka.learnnplay.domain.entity.Test;
import com.bodanka.learnnplay.domain.entity.User;
import com.bodanka.learnnplay.domain.entity.UserTestGrade;
import com.bodanka.learnnplay.service.TestService;
import com.bodanka.learnnplay.service.UserService;
import com.bodanka.learnnplay.service.UserTestGradeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PreAuthorize("hasRole('STUDENT')")
@RestController
@RequiredArgsConstructor
@RequestMapping("/grades")
public class UserTestGradeController {
    private final UserTestGradeService userTestGradeService;
    private final TestService testService;
    private final UserService userService;

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
}
