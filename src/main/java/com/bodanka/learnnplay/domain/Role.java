package com.bodanka.learnnplay.domain;

import java.util.Arrays;

public enum Role {
    STUDENT, TEACHER;

    public static Role fromString(String role) {
        return Arrays.stream(values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid role: " + role));
    }
}
