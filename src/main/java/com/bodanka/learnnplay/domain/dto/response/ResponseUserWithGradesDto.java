package com.bodanka.learnnplay.domain.dto.response;

import java.util.List;

public record ResponseUserWithGradesDto(String studentId, String firstName, String lastName, Double totalGrade,
                                        List<ResponseClassWithGradesDto> classesGrades) {
}
