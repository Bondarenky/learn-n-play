package com.bodanka.learnnplay.service;

import com.bodanka.learnnplay.domain.entity.Theme;
import com.bodanka.learnnplay.domain.entity.User;
import com.bodanka.learnnplay.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class DefaultThemeService implements ThemeService {
    private final ThemeRepository themeRepository;
    private final UserTestGradeService userTestGradeService;

    @Override
    public Theme saveTheme(Theme theme) {
        return themeRepository.save(theme);
    }

    @Override
    public Optional<Theme> findThemeById(String id) {
        return themeRepository.findById(id);
    }

    @Override
    public List<Theme> findByUser(User user) {
        List<Theme> themes = themeRepository.findByUser(user);
        themes.sort(Comparator.comparingInt(value -> value.getClazz().getGrade().getGradeValue()));
        return themes;
    }

    @Override
    public String deleteThemeById(String id) {
        AtomicReference<String> title = new AtomicReference<>();
        AtomicBoolean hasTests = new AtomicBoolean(false);
        findThemeById(id).ifPresent(theme -> {
            title.set(theme.getTitle());
            hasTests.set(!theme.getTests().isEmpty());
            themeRepository.delete(theme);
            userTestGradeService.deleteByThemeId(id);
        });
        return "Theme %s%s were deleted".formatted(title.get(), hasTests.get() ? " and its tests" : "");
    }
}
