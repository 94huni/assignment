package com.example.board.data.responseDto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MemberResponse {
    private int id;
    private String nickname;
    private String userName;
    private String email;
    private String phone;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
