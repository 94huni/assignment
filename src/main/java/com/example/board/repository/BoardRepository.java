package com.example.board.repository;

import com.example.board.data.entity.Board;
import com.example.board.data.responseDto.BoardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    Page<Board> findAllByOrderByCreateAtDesc(Pageable pageable);

    Page<Board> findBoardByTitleContainingOrderByCreateAtDesc(String titleKeyword, Pageable pageable);


}
