package com.bodanka.learnnplay.domain.dto.request;

import com.bodanka.learnnplay.domain.QuestionAnswer;

import java.util.List;

public record RequestTestAnswersDto(String testId, List<QuestionAnswer> questions) {
}
