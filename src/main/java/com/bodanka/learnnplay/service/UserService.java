package com.bodanka.learnnplay.service;

import com.bodanka.learnnplay.domain.Grade;
import com.bodanka.learnnplay.domain.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    String deleteById(UUID id);

    void enable(UUID id);

    void addStudent(User teacher, User student);

    List<User> findByTeacher(UUID teacherId);

    void increaseGradeByUserId(String userId, Grade grade);
}
