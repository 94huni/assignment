package com.example.board.service;

import com.example.board.data.entity.Member;
import com.example.board.data.entity.Role;
import com.example.board.data.requestDto.MemberSignUp;
import com.example.board.data.requestDto.MemberUpdate;
import com.example.board.exception.CustomException;
import com.example.board.exception.ErrorCode;
import com.example.board.repository.MemberRepository;
import com.example.board.service.impl.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service("MemberService")
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void signUpMember(MemberSignUp memberSignUp) {
        try {
            if (!memberSignUp.getPassword().equals(memberSignUp.getValidPassword()))
                throw new CustomException(ErrorCode.DIFFERENT_PASSWORD);

            if(memberRepository.existsByNickname(memberSignUp.getNickName()))
                throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);

            if(memberRepository.existsByEmail(memberSignUp.getEmail()))
                throw new CustomException(ErrorCode.DUPLICATED_EMAIL);

            Member member = Member.builder()
                    .email(memberSignUp.getEmail())
                    .nickname(memberSignUp.getNickName())
                    .userName(memberSignUp.getUserName())
                    .phone(memberSignUp.getPhone())
                    .password(passwordEncoder.encode(memberSignUp.getPassword()))
                    .role(Role.Member)
                    .createAt(LocalDateTime.now())
                    .build();

            memberRepository.save(member);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    @Override
    public boolean validNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    @Override
    public boolean validPassword(String password, String validPassword) {
        return password.equals(validPassword);
    }

    @Override
    @Transactional
    public void updateMember(MemberUpdate memberUpdate, Member member) {
        try {
            if (!memberUpdate.getPassword().equals(memberUpdate.getValidPassword()))
                throw new CustomException(ErrorCode.DIFFERENT_PASSWORD);

            if(memberRepository.existsByNickname(memberUpdate.getNickname()))
                throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);

            Member result = Member.builder()
                    .mId(member.getMId())
                    .nickname(memberUpdate.getNickname())
                    .password(passwordEncoder.encode(memberUpdate.getPassword()))
                    .updateAt(LocalDateTime.now())
                    .build();

            memberRepository.save(result);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
