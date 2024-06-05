package com.bodanka.learnnplay.service;

import com.bodanka.learnnplay.domain.Grade;
import com.bodanka.learnnplay.domain.entity.Class;
import com.bodanka.learnnplay.repository.ClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultClassService implements ClassService {
    private final ClassRepository classRepository;

    @Override
    public Class save(Class clazz) {
        return classRepository.save(clazz);
    }

    @Override
    public Optional<Class> findByGrade(Grade grade) {
        return classRepository.findByGrade(grade);
    }

    @Override
    public Optional<Grade> findGradeByClassId(String id) {
        return classRepository.findById(id).map(Class::getGrade);
    }
}
