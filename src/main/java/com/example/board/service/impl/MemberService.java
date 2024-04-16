package com.example.board.service.impl;

import com.example.board.data.entity.Member;
import com.example.board.data.requestDto.MemberSignUp;
import com.example.board.data.requestDto.MemberUpdate;
import com.example.board.data.responseDto.MemberResponse;

public interface MemberService {

    MemberResponse getMember(String email);

    void signUpMember(MemberSignUp memberSignUp);

    boolean validNickname(String nickname);

    boolean validPassword(String password, String validPassword);

    boolean validEmail(String email);

    void updateMember(MemberUpdate memberUpdate, Member member);
}
