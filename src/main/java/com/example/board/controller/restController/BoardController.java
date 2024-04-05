package com.example.board.controller.restController;

import com.example.board.data.responseDto.BoardResponse;
import com.example.board.service.impl.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api-v2")
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/detail/{bId}")
    public ResponseEntity<BoardResponse> getBoard(@PathVariable int bId) {
        return ResponseEntity.ok(boardService.getBoard(bId));
    }
}
