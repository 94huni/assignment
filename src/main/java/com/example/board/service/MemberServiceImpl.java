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
        // 토큰에 저장된 email 가져오기
        String email = jwtProvider.getUsername(token);

        // select * from MEMBER where email = {email};
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
    }

    // 현재 접속된 유저 반환
    @Override
    public Member principalMember(Principal principal) {
        // Principal 객체는 현재 Security Context 에 보관된 정보
        // getName() 메서드는 현재 Member Entity 에서 상속받고있는 UserDetails 의 getName 이기 때문에 현재는 email 을 반환함
        return memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
    }

    // 토큰으로 현재 접속된 email 반환
    @Override
    public String currentMember(String token) {
        String email =  jwtProvider.getUsername(token);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
        return member.getNickname();
    }

    // 로그인
    @Override
    public String signIn(SignIn signIn) {
        try {

            /*
            현재 입력받은 아이디와 비밀번호를 가지고 spring security 의 UsernamePasswordAuthenticationToken 사용하여
             AuthenticationManager 반환
             */
            manager.authenticate(new
                    UsernamePasswordAuthenticationToken(signIn.getEmail(), signIn.getPassword()));


            Member member = memberRepository.findByEmail(signIn.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Member Not Found"));


            return jwtProvider.createToken(signIn.getEmail(), member.getRole());

        } catch (UsernameNotFoundException e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
        } catch (AuthenticationException e) {
            // UsernamePasswordAuthenticationToken 실패시 발생하는 에러 처리
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
        // email 정보로 필요한 Member 객체 생성
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        // MemberResponse 에 필요한 값 저장후 반환
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
            // 패스워드 검증 시 같지않으면
            if (!memberSignUp.getPassword().equals(memberSignUp.getValidPassword()))
                throw new CustomException(ErrorCode.DIFFERENT_PASSWORD);
            // 중복된 닉네임
            if(memberRepository.existsByNickname(memberSignUp.getNickName()))
                throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
            // 중복된 이메일
            if(memberRepository.existsByEmail(memberSignUp.getEmail()))
                throw new CustomException(ErrorCode.DUPLICATED_EMAIL);

            // 조건을 만족하면 받아온 회원가입 정보를 가지고 새로운 데이터 생성
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

            // 비밀번호 검증
            if (!memberUpdate.getPassword().equals(memberUpdate.getValidPassword()))
                throw new CustomException(ErrorCode.DIFFERENT_PASSWORD);

            // 닉네임만 변경 가능함으로 닉네임 중복확인
            if(memberRepository.existsByNickname(memberUpdate.getNickname()) && memberUpdate.getNickname() != null)
                throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);

            // 닉네임의 값은 안들어올 수도 있기 때문에 null 값 요청시 member 에 저장된 nickname 사용
            String nickname = (null == memberUpdate.getNickname()) ? member.getNickname() : memberUpdate.getNickname();

            // toBuilder 사용하여 바뀐부분 바꿔줌
            Member result = member.toBuilder()
                    .mId(member.getMId())
                    .nickname(nickname)
                    .password(passwordEncoder.encode(memberUpdate.getPassword()))
                    .updateAt(LocalDateTime.now())
                    .build();


            memberRepository.save(result);
        } catch (CustomException e) {

            log.error(e.getMessage());
            throw new CustomException(e.getErrorCode());

        } catch (Exception e) {

            log.error(e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);

        }
    }
}
