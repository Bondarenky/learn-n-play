package com.bodanka.learnnplay.service;

import com.bodanka.learnnplay.domain.entity.Theme;
import com.bodanka.learnnplay.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultThemeService implements ThemeService {
    private final ThemeRepository themeRepository;

    @Override
    public Theme saveTheme(Theme theme) {
        return themeRepository.save(theme);
    }

    @Override
    public Optional<Theme> findThemeById(String id) {
        return themeRepository.findById(id);
    }
}
