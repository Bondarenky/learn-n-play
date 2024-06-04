package com.bodanka.learnnplay.service;

import com.bodanka.learnnplay.domain.entity.Test;
import com.bodanka.learnnplay.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultTestService implements TestService {
    private final TestRepository testRepository;

    @Override
    public Test save(Test test) {
        return testRepository.save(test);
    }

    @Override
    public Optional<Test> findById(String id) {
        return testRepository.findById(id);
    }
}
