package com.example.board.service;

import com.example.board.config.jwt.JwtProvider;
import com.example.board.data.entity.Member;
import com.example.board.data.entity.Role;
import com.example.board.data.requestDto.MemberSignUp;
import com.example.board.data.requestDto.MemberUpdate;
import com.example.board.data.requestDto.SignIn;
import com.example.board.data.responseDto.MemberResponse;
import com.example.board.exception.CustomException;
import com.example.board.exception.ErrorCode;
import com.example.board.repository.MemberRepository;
import com.example.board.service.impl.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.security.Principal;
import java.time.LocalDateTime;

@Service("MemberService")
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager manager;
    private final JwtProvider jwtProvider;

    // 토큰으로 받아왔을때 사용
    @Override
    public Member findMember(String token) {
        String email = jwtProvider.getUsername(token);

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
    }

    // 현재 접속된 유저 반환
    @Override
    public Member principalMember(Principal principal) {
        return memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
    }

    // 토큰으로 현재 접속된 email 반환
    @Override
    public String currentMember(String token) {
        return jwtProvider.getUsername(token);
    }

    // 로그인
    @Override
    public String signIn(SignIn signIn) {
        try {

            manager.authenticate(new
                    UsernamePasswordAuthenticationToken(signIn.getEmail(), signIn.getPassword()));

            Member member = memberRepository.findByEmail(signIn.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Member Not Found"));


            return jwtProvider.createToken(signIn.getEmail(), member.getRole());

        } catch (UsernameNotFoundException e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
        } catch (AuthenticationException e) {

            log.error(e.getMessage());
            throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);

        }
    }

    // 유저에게 보여주기위한 정보 반환
    @Override
    public MemberResponse getMember(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        return MemberResponse.builder()
                .id(member.getMId())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .userName(member.getUsername())
                .phone(member.getPhone())
                .createAt(member.getCreateAt())
                .updateAt(member.getUpdateAt())
                .build();
    }

    // 회원가입
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
                    .role(Role.MEMBER)
                    .createAt(LocalDateTime.now())
                    .build();

            memberRepository.save(member);
        }catch (CustomException e) {

            log.error(e.getMessage());
            throw new CustomException(e.getErrorCode());

        }catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }

    // 닉네임 중복확인
    @Override
    public boolean validNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    // 이메일 중복확인
    @Override
    public boolean validEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    // 회원정보 수정
    @Override
    @Transactional
    public void updateMember(MemberUpdate memberUpdate, Member member) {
        try {
            if (!memberUpdate.getPassword().equals(memberUpdate.getValidPassword()))
                throw new CustomException(ErrorCode.DIFFERENT_PASSWORD);

            if(memberRepository.existsByNickname(memberUpdate.getNickname()))
                throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);


            Member result = member.toBuilder()
                    .mId(member.getMId())
                    .nickname(memberUpdate.getNickname())
                    .password(passwordEncoder.encode(memberUpdate.getPassword()))
                    .updateAt(LocalDateTime.now())
                    .build();


            memberRepository.save(result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
