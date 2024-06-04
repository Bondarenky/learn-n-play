package com.bodanka.learnnplay.domain.dto.request;

import java.util.List;

public record RequestTestDto(String title, String sectionId, List<RequestQuestionDto> tests) {
}
