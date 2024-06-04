package com.bodanka.learnnplay.service;

import com.bodanka.learnnplay.domain.Grade;
import com.bodanka.learnnplay.domain.entity.Class;

import java.util.Optional;

public interface ClassService {
    Class save(Class clazz);

    Optional<Class> findByGrade(Grade grade);
}
