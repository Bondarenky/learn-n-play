package com.bodanka.learnnplay.domain.dto.response;

import java.util.List;

public record ResponseClassSectionDto(
        String id,
        String title,
        Double percentage,
        List<ResponseTestDto> tests
) {
}
