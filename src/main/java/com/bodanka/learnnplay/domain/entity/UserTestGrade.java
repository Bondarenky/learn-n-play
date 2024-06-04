package com.bodanka.learnnplay.domain.entity;

import com.bodanka.learnnplay.domain.UserTestGradeId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(UserTestGradeId.class)
@Table(name = "user_test_grades")
public class UserTestGrade {
    @Id
    @Column(length = 36)
    private String userId;

    @Id
    @Column(length = 36)
    private String classId;

    @Id
    @Column(length = 36)
    private String themeId;

    @Id
    @Column(length = 36)
    private String testId;

    @Id
    @Column(precision = 2)
    private double percentage;
}
