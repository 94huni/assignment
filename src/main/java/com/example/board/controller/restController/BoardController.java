package com.example.board.controller.restController;

import com.example.board.data.entity.Member;
import com.example.board.data.requestDto.BoardCreate;
import com.example.board.data.requestDto.BoardUpdate;
import com.example.board.data.responseDto.BoardResponse;
import com.example.board.data.responseDto.MemberResponse;
import com.example.board.service.impl.BoardService;
import com.example.board.service.impl.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api-v2")
public class BoardController {
    private final BoardService boardService;
    private final MemberService memberService;

    @GetMapping("/detail/{bId}")
    public ResponseEntity<BoardResponse> getBoard(@PathVariable int bId) {
        return ResponseEntity.ok(boardService.getBoard(bId));
    }

    @GetMapping("/list")
    public ResponseEntity<Page<BoardResponse>> getBoards(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(boardService.boardList(keyword, page));
    }

}
