package com.bodanka.learnnplay.domain.dto.response;

import com.bodanka.learnnplay.domain.Question;

import java.util.List;

public record ResponseSingleTestDto(String id, int course, String title, String sectionTitle,
                                    List<Question> questions) {
}
