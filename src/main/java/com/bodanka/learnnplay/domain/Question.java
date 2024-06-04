package com.bodanka.learnnplay.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Question(
        int id,
        String question,
        List<String> answers
) {
}
