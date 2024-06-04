package com.bodanka.learnnplay.repository;

import com.bodanka.learnnplay.domain.entity.Theme;
import com.bodanka.learnnplay.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, String> {
    List<Theme> findByUser(User user);
}
