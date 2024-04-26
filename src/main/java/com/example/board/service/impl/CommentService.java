package com.example.board.service.impl;

import com.example.board.data.entity.Board;
import com.example.board.data.entity.Member;
import com.example.board.data.requestDto.CommentWrite;
import com.example.board.data.responseDto.CommentResponse;
import org.springframework.data.domain.Page;

public interface CommentService {

    void createComment(CommentWrite create, Member member, Board board);

    Page<CommentResponse> getCommentsByBoard(int b_id, int page);

    Page<CommentResponse> getCommentsByMember(String email, int page);

    void updateComment(int cId, CommentWrite create, Member member);

    void deleteComment(int cId, Member member);
}
