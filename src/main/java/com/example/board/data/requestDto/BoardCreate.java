package com.example.board.data.requestDto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class BoardCreate {
    @Size(max = 30, min = 2)
    private String title;

    @Size(max = 255, min = 1)
    private String content;
}
