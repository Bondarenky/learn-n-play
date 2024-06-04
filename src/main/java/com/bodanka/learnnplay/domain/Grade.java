package com.bodanka.learnnplay.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Grade {
    ELEVEN(11, null),
    TEN(10, ELEVEN),
    NINE(9, TEN),
    EIGHT(8, NINE),
    SEVEN(7, EIGHT),
    SIX(6, SEVEN);

    private final int gradeValue;
    private final Grade next;

    Grade(int gradeValue, Grade next) {
        this.gradeValue = gradeValue;
        this.next = next;
    }

    public static Grade fromGradeValue(int gradeValue) {
        return Arrays.stream(values())
                .filter(grade -> grade.gradeValue == gradeValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid class grade: " + gradeValue));
    }

    public Grade getNext() {
        return next == null ? ELEVEN : next;
    }
}
