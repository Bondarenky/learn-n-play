package com.bodanka.learnnplay.service;

import com.bodanka.learnnplay.domain.entity.Theme;
import com.bodanka.learnnplay.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface ThemeService {
    Theme saveTheme(Theme theme);

    Optional<Theme> findThemeById(String id);

    List<Theme> findByUser(User user);

    String deleteThemeById(String id);
}
