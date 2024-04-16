package com.example.board.service.impl;

import com.example.board.data.entity.Member;
import com.example.board.data.requestDto.MemberSignUp;
import com.example.board.data.requestDto.MemberUpdate;

public interface MemberService {

    void signUpMember(MemberSignUp memberSignUp);

    boolean validNickname(String nickname);

    boolean validPassword(String password, String validPassword);

    void updateMember(MemberUpdate memberUpdate, Member member);
}
