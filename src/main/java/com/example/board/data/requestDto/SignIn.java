package com.example.board.data.requestDto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class SignIn {
    @Email(message = "Not in email format")
    @NotNull
    private String email;
    @NotNull
    private String password;
}
