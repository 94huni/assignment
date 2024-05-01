package com.example.board.service;

import com.example.board.exception.CustomException;
import com.example.board.exception.ErrorCode;
import com.example.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService{

    private final MemberRepository memberRepository;

    // UserDetailsService 구현
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)); // Email 이 없을시 NOT FOUND
    }

}
