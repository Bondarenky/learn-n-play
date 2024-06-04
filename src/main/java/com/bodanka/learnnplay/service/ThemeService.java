package com.bodanka.learnnplay.service;

import com.bodanka.learnnplay.domain.entity.Theme;

import java.util.Optional;

public interface ThemeService {
    Theme saveTheme(Theme theme);

    Optional<Theme> findThemeById(String id);
}
