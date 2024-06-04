package com.bodanka.learnnplay.controller;

import com.bodanka.learnnplay.domain.Question;
import com.bodanka.learnnplay.domain.dto.request.RequestQuestionDto;
import com.bodanka.learnnplay.domain.dto.request.RequestTestDto;
import com.bodanka.learnnplay.domain.dto.response.ResponseSingleTestDto;
import com.bodanka.learnnplay.domain.dto.response.ResponseThemeTestDto;
import com.bodanka.learnnplay.domain.entity.Test;
import com.bodanka.learnnplay.domain.entity.Theme;
import com.bodanka.learnnplay.service.TestService;
import com.bodanka.learnnplay.service.ThemeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tests")
public class ThemeTestController {
    private final TestService testService;
    private final ThemeService themeService;

    @PostMapping
    public ResponseEntity<ResponseThemeTestDto> saveTest(@RequestBody RequestTestDto dto) {
        try {
            Theme theme = themeService.findThemeById(dto.sectionId()).orElseThrow(
                    () -> new RuntimeException("Theme [%s] not found".formatted(dto.sectionId()))
            );
            ObjectMapper objectMapper = new ObjectMapper();
            String questions = objectMapper.writeValueAsString(dto.tests());
            Test test = testService.save(new Test(dto.title(), theme, questions));
            List<RequestQuestionDto> questionList = objectMapper.readValue(questions, new TypeReference<>() {
            });
            return new ResponseEntity<>(new ResponseThemeTestDto(test.getId(), test.getTitle(), questionList), HttpStatus.CREATED);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse questions: ", e);
        }
    }

    @GetMapping("/{testId}")
    public ResponseEntity<ResponseSingleTestDto> findTestById(@PathVariable("testId") String testId) {
        try {
            Test test = testService.findById(testId).orElseThrow(
                    () -> new RuntimeException("Test [%s] not found".formatted(testId))
            );
            ObjectMapper objectMapper = new ObjectMapper();
            List<Question> questions = objectMapper.readValue(test.getQuestions(), new TypeReference<>() {
            });
            return ResponseEntity.ok(new ResponseSingleTestDto(
                    test.getId(),
                    test.getTheme().getClazz().getGrade().getGradeValue(),
                    test.getTitle(),
                    test.getTheme().getTitle(),
                    questions
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse questions: ", e);
        }
    }
}
