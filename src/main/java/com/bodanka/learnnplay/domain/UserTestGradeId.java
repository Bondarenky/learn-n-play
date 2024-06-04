package com.bodanka.learnnplay.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserTestGradeId implements Serializable {
    private String userId;
    private String classId;
    private String themeId;
    private String testId;
}
