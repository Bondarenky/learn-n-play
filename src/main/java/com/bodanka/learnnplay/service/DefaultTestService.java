package com.bodanka.learnnplay.service;

import com.bodanka.learnnplay.domain.entity.Test;
import com.bodanka.learnnplay.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class DefaultTestService implements TestService {
    private final TestRepository testRepository;
    private final UserTestGradeService userTestGradeService;

    @Override
    public Test save(Test test) {
        return testRepository.save(test);
    }

    @Override
    public Optional<Test> findById(String id) {
        return testRepository.findById(id);
    }

    @Override
    public String delete(String id) {
        AtomicReference<String> testTitle = new AtomicReference<>("");
        findById(id).ifPresent(test -> {
            testTitle.set(test.getTitle());
            testRepository.delete(test);
            userTestGradeService.deleteByTestId(id);
        });
        return "Test %s were deleted".formatted(testTitle.get());
    }
}
