package com.bodanka.learnnplay.domain.dto.request;

public record RequestUserDto(
        String firstName,
        String lastName,
        String email,
        String password,
        String role
) {
}
