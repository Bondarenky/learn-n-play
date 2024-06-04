package com.bodanka.learnnplay.domain.dto.response;

import java.util.List;

public record ResponseClassDto(
        String id,
        int grade,
        List<ResponseClassSectionDto> sections
) {
}
