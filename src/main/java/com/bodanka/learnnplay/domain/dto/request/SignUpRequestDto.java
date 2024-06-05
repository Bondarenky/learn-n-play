package com.bodanka.learnnplay.domain.dto.request;

public record SignUpRequestDto(String firstName, String lastName, String email, String password, String confirmPassword) {
}
