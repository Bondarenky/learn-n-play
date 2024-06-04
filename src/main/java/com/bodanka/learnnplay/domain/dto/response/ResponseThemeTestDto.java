package com.bodanka.learnnplay.domain.dto.response;

import com.bodanka.learnnplay.domain.dto.request.RequestQuestionDto;

import java.util.List;

public record ResponseThemeTestDto(String id, String title, List<RequestQuestionDto> questions) {
}
