package com.bodanka.learnnplay.domain.dto.response;

import java.util.List;

public record ResponseClassWithGradesDto(int grade, Double classGrade, List<ResponseThemeWithGradeDto> themesGrades) {
}
