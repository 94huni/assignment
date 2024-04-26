package com.example.board.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    DUPLICATED_NICKNAME(HttpStatus.CONFLICT, "nickname is duplicated"),
    DIFFERENT_PASSWORD(HttpStatus.CONFLICT, "password is duplicated"),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "Email is duplicated"),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "Email is Not found"),
    NOT_FOUND_BOARD(HttpStatus.NOT_FOUND, "Board is Not found"),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "Comment is Not found"),
    NOT_FORBIDDEN_MEMBER(HttpStatus.FORBIDDEN, "No access rights"),
    UNAUTHORIZED_MEMBER(HttpStatus.UNAUTHORIZED, "Please check your Email or Password"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request");


    private final HttpStatus status;
    private final String message;
}
