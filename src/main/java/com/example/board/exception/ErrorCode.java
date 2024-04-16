package com.example.board.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    DUPLICATED_NICKNAME(HttpStatus.CONFLICT, " nickname is duplicated"),
    DIFFERENT_PASSWORD(HttpStatus.CONFLICT, "password is duplicated"),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "Email is duplicated"),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "Email is Not found"),
    NOT_UNAUTHORIZED_MEMBER(HttpStatus.UNAUTHORIZED, "Member is UNAUTHORIZED");


    private final HttpStatus status;
    private final String message;
}
