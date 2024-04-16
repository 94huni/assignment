package com.example.board.data.requestDto;

import lombok.Data;


@Data
public class MemberSignUp {
    
    private String userName;

    private String nickName;

    private String password;

    private String validPassword;

    private String phone;

    private String email;
}
