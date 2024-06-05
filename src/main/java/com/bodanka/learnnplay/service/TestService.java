package com.bodanka.learnnplay.service;

import com.bodanka.learnnplay.domain.entity.Test;

import java.util.Optional;

public interface TestService {
    Test save(Test test);

    Optional<Test> findById(String id);

    String delete(String id);
}
