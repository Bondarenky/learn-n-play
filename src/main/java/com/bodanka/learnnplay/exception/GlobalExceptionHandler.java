package com.bodanka.learnnplay.exception;

import com.bodanka.learnnplay.domain.dto.response.ExceptionResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponseDto> handleBadRequestException(BadRequestException e) {
        return createResponse(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return createResponse(HttpStatus.BAD_REQUEST, "Bad Credentials");
    }

    private ResponseEntity<ExceptionResponseDto> createResponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new ExceptionResponseDto(httpStatus, message), httpStatus);
    }
}
