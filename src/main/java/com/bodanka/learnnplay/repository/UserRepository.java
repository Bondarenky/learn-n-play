package com.bodanka.learnnplay.repository;

import com.bodanka.learnnplay.domain.Grade;
import com.bodanka.learnnplay.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User u " +
            "SET u.enabled = TRUE " +
            "WHERE u.id = ?1")
    void enable(String id);

    List<User> findByTeacherIdAndEnabledTrue(String teacherId);

    @Transactional
    @Modifying
    @Query("UPDATE User u " +
            "SET u.currentGrade = ?2 " +
            "WHERE u.id = ?1")
    void increaseGradeByUserId(String userId, Grade grade);
}
