package com.example.board.data.responseDto;

import com.example.board.data.entity.Board;
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
