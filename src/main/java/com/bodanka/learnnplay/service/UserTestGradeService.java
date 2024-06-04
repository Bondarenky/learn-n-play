package com.bodanka.learnnplay.service;

import com.bodanka.learnnplay.domain.entity.UserTestGrade;

import java.util.List;

public interface UserTestGradeService {
    UserTestGrade save(UserTestGrade userTestGrade);

    List<UserTestGrade> findByUserIdAndClassId(String userId, String classId);
}