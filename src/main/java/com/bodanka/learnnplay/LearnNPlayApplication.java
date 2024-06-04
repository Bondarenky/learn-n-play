package com.bodanka.learnnplay;

import com.bodanka.learnnplay.domain.Grade;
import com.bodanka.learnnplay.domain.entity.Class;
import com.bodanka.learnnplay.service.ClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class LearnNPlayApplication implements CommandLineRunner {
    private final ClassService classService;

    public static void main(String[] args) {
        SpringApplication.run(LearnNPlayApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        classService.findByGrade(Grade.SIX).ifPresentOrElse(this::log, () -> saveAndLog(Grade.SIX));
        classService.findByGrade(Grade.SEVEN).ifPresentOrElse(this::log, () -> saveAndLog(Grade.SEVEN));
        classService.findByGrade(Grade.EIGHT).ifPresentOrElse(this::log, () -> saveAndLog(Grade.EIGHT));
        classService.findByGrade(Grade.NINE).ifPresentOrElse(this::log, () -> saveAndLog(Grade.NINE));
        classService.findByGrade(Grade.TEN).ifPresentOrElse(this::log, () -> saveAndLog(Grade.TEN));
        classService.findByGrade(Grade.ELEVEN).ifPresentOrElse(this::log, () -> saveAndLog(Grade.ELEVEN));
    }

    private void log(Class clazz) {
        log.info("Class {} exists, skipping...", clazz.getGrade());
    }

    private void saveAndLog(Grade grade) {
        Class save = classService.save(new Class(grade));
        log.info("Class {} saved", save.getGrade());
    }
}
