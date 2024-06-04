package com.bodanka.learnnplay.repository;

import com.bodanka.learnnplay.domain.entity.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, String> {
}
