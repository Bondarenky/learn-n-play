package com.bodanka.learnnplay.domain.mapper;

public interface Mapper<E, REQ, RES> {
    E toEntity(REQ requestDto);

    RES toResponseDto(E entity);
}
