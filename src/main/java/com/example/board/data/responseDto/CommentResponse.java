package com.example.board.data.responseDto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponse {
    private int cId;
    private String comment;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private int bId;
    private String writer;
}
