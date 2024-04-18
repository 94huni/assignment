package com.example.board.repository;

import com.example.board.data.entity.Board;
import com.example.board.data.responseDto.BoardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    Page<Board> findAllByBIdDesc(Pageable pageable);
    Page<Board> findBoardByTitleOrContentContainingOrderByBIdDesc(String keyword, Pageable pageable);

}
