package com.bodanka.learnnplay.domain.dto.response;

import java.util.List;

public record ResponseThemeWithTestsDto(String id, String title, List<String> tests) {
}
