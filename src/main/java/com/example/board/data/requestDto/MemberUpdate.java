package com.example.board.data.requestDto;

import lombok.Data;

@Data
public class MemberUpdate {
    String nickname;
    String password;
    String validPassword;
}
