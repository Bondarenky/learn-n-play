package com.bodanka.learnnplay.domain.mapper;

import com.bodanka.learnnplay.domain.Role;
import com.bodanka.learnnplay.domain.dto.request.RequestUserDto;
import com.bodanka.learnnplay.domain.dto.response.ResponseUserDto;
import com.bodanka.learnnplay.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements Mapper<User, RequestUserDto, ResponseUserDto> {
    @Override
    public User toEntity(RequestUserDto requestDto) {
        return new User(
                requestDto.firstName(),
                requestDto.lastName(),
                requestDto.email(),
                requestDto.password(),
                Role.fromString(requestDto.role())
        );
    }

    @Override
    public ResponseUserDto toResponseDto(User entity) {
        return new ResponseUserDto(
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getRole().name()
        );
    }
}
