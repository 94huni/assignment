package com.example.board.repository;

import com.example.board.data.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    Member findByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);
}
