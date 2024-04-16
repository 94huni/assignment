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
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "Email is duplicated");

    private final HttpStatus status;
    private final String message;
}
