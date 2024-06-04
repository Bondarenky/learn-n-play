package com.bodanka.learnnplay.domain.dto.request;

public record TeacherSignUpRequestDto(String firstName, String lastName, String email, String password, String confirmPassword) {
}
