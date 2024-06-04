package com.bodanka.learnnplay.domain;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class UserTestGradeId implements Serializable {
    private String userId;
    private String classId;
    private String themeId;
    private String testId;
    private double percentage;
}
