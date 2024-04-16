package com.example.board.data.requestDto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Data
public class MemberSignUp {
    @NotNull
    private String userName;

    @NotNull
    private String nickName;

    @NotNull
    private String password;

    @NotNull
    private String validPassword;

    @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$"
            , message = "Only 10 to 11 digits can be entered")
    @NotNull
    private String phone;

    @Email(message = "Not in email format")
    @NotNull
    private String email;
}
