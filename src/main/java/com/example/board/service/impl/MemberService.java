package com.example.board.service.impl;

import com.example.board.data.entity.Member;
import com.example.board.data.requestDto.MemberSignUp;
import com.example.board.data.requestDto.MemberUpdate;
import com.example.board.data.requestDto.SignIn;
import com.example.board.data.responseDto.MemberResponse;

import java.security.Principal;

public interface MemberService {

    MemberResponse getMember(String email);

    Member principalMember(Principal principal);

    String currentMember(String token);

    Member findMember(String token);

    String signIn(SignIn signIn);

    void signUpMember(MemberSignUp memberSignUp);

    boolean validNickname(String nickname);

    boolean validPassword(String password, String validPassword);

    boolean validEmail(String email);

    void updateMember(MemberUpdate memberUpdate, String token);
}
