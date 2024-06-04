package com.bodanka.learnnplay.repository;

import com.bodanka.learnnplay.domain.Grade;
import com.bodanka.learnnplay.domain.entity.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<Class, String> {
    Optional<Class> findByGrade(Grade grade);
}
