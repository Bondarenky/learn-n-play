package com.bodanka.learnnplay.domain.dto.response;

public record ResponseUserDto(
        String firstName,
        String lastName,
        String email,
        String role
) {
}
