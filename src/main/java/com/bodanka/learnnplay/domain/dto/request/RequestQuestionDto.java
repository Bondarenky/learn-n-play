package com.bodanka.learnnplay.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RequestQuestionDto(String question, @JsonProperty("correct_answer") String correctAnswer,
                                 List<String> answers) {
}
