package com.example.board.repository;

import com.example.board.data.entity.Board;
import com.example.board.data.entity.Comment;
import com.example.board.data.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findCommentByBoardOrderByCreateAtDesc(Board board, Pageable pageable);

    Page<Comment> findCommentByMemberOrderByCreateAtDesc(Member member, Pageable pageable);


}
