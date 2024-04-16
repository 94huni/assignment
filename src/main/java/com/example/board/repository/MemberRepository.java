package com.example.board.repository;

import com.example.board.data.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {

    Optional<Member> findByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);
}
