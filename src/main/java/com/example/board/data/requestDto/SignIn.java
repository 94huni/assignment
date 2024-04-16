package com.example.board.data.requestDto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
public class SignIn {
    @Email(message = "Not in email format")
    private String email;
    private String password;
}
