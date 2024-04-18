package com.example.board.service.impl;

import com.example.board.data.entity.Member;
import com.example.board.data.requestDto.BoardCreate;
import com.example.board.data.requestDto.BoardUpdate;
import com.example.board.data.responseDto.BoardResponse;
import com.example.board.data.responseDto.MemberResponse;
import org.springframework.data.domain.Page;

public interface BoardService {
    BoardResponse getBoard(int bId);
    void createBoard(BoardCreate create, Member member);
    void updateBoard(int bId, BoardUpdate boardUpdate, MemberResponse member);
    void deleteBoard(int bId, Member member);
    Page<BoardResponse> boardList(String keyword, int page);
}
