package com.example.board.service;

import com.example.board.data.entity.Board;
import com.example.board.data.responseDto.BoardResponse;
import com.example.board.repository.BoardRepository;
import com.example.board.service.impl.BoardService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("BoardService")
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;

    private BoardResponse toEntity(Board board) {
        BoardResponse boardResponse = new BoardResponse();
        boardResponse.setNickname(board.getMember().getNickname());
        boardResponse.setTitle(board.getTitle());
        boardResponse.setContent(board.getContent());
        boardResponse.setCreateAt(board.getCreateAt());
        boardResponse.setUpdateAt(board.getUpdateAt());

        return boardResponse;
    }

    @Override
    public BoardResponse getBoard(int bId) {
        Board board = boardRepository.getById(bId);
        return toEntity(board);
    }
}
