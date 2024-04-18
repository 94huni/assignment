package com.example.board.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    DUPLICATED_NICKNAME(HttpStatus.CONFLICT, "409 nickname is duplicated"),
    DIFFERENT_PASSWORD(HttpStatus.CONFLICT, "409 password is duplicated"),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "409 Email is duplicated"),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "404 Email is Not found"),
    NOT_FORBIDDEN_MEMBER(HttpStatus.FORBIDDEN, "403 No access rights"),
    UNAUTHORIZED_MEMBER(HttpStatus.UNAUTHORIZED, "401 Please check your Email or Password"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500 INTERNAL_SERVER_ERROR");


    private final HttpStatus status;
    private final String message;
}
