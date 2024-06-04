package com.bodanka.learnnplay.repository;

import com.bodanka.learnnplay.domain.UserTestGradeId;
import com.bodanka.learnnplay.domain.entity.UserTestGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTestGradeRepository extends JpaRepository<UserTestGrade, UserTestGradeId> {
    List<UserTestGrade> findByUserIdAndClassId(String userId, String classId);
}
