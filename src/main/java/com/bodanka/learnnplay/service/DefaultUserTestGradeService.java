package com.bodanka.learnnplay.service;

import com.bodanka.learnnplay.domain.entity.UserTestGrade;
import com.bodanka.learnnplay.repository.UserTestGradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultUserTestGradeService implements UserTestGradeService {
    private final UserTestGradeRepository userTestGradeRepository;

    @Override
    public UserTestGrade save(UserTestGrade userTestGrade) {
        return userTestGradeRepository.save(userTestGrade);
    }

    @Override
    public List<UserTestGrade> findByUserIdAndClassId(String userId, String classId) {
        return userTestGradeRepository.findByUserIdAndClassId(userId, classId);
    }
}