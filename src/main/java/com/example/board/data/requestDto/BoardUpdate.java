package com.example.board.data.requestDto;

import lombok.Data;

@Data
public class BoardUpdate {
    private int bId;
    private String title;
    private String content;
}
