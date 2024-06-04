package com.bodanka.learnnplay.domain.dto.response;

public record ResponseCurrentUserDto(
        String firstName,
        String lastName,
        String email,
        String role,
        int grade
) {
}
