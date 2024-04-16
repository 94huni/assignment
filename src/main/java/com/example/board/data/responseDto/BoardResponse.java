package com.example.board.data.responseDto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardResponse {
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

}
