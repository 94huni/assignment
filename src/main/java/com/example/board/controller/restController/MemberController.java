package com.example.board.controller.restController;

import com.example.board.data.entity.Member;
import com.example.board.data.requestDto.MemberSignUp;
import com.example.board.data.requestDto.MemberUpdate;
import com.example.board.data.requestDto.SignIn;
import com.example.board.exception.CustomException;
import com.example.board.exception.ErrorCode;
import com.example.board.service.AccountService;
import com.example.board.service.impl.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/member")
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final AccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<String> loginMember(@RequestBody @Valid SignIn signIn) {
        Member member = memberService.findMember(signIn);
        log.info("Member Email : {}", signIn.getEmail());
        Member result = (Member) accountService.loadUserByUsername(member.getEmail());
        return ResponseEntity.ok(result.getUsername() + " login successful");
    }

    @PostMapping("/signUp")
    public ResponseEntity<String> memberSignUp(@Valid @RequestBody MemberSignUp memberSignUp) {
        memberService.signUpMember(memberSignUp);

        log.info("MemberEmail : {}", memberSignUp.getEmail());

        return ResponseEntity.ok("Membership registration completed");
    }

    @PutMapping("/update/{email}")
    public ResponseEntity<String> memberUpdate(@PathVariable String email,
                                               Principal principal,
                                               @Valid @RequestBody MemberUpdate memberUpdate) {

        if (email.equals(principal.getName())) {
            memberService.getMember(email);
        } else {
            throw new CustomException(ErrorCode.NOT_FORBIDDEN_MEMBER);
        }

        memberService.updateMember(memberUpdate, (Member) principal);
        log.info("principal email : {}", ((Member) principal).getEmail());

        return ResponseEntity.ok("Successfully changed membership information");
    }

    @GetMapping("/validNickname")
    public int validNickname(String nickname) {
        if (memberService.validNickname(nickname))
            return 1;
        else
            return 0;
    }

    @GetMapping("/validEmail")
    public int validEmail(String email) {
        if (memberService.validEmail(email))
            return 1;
        else
            return 0;
    }

    @GetMapping("/validPassword")
    public int validPassword(String password, String validPassword) {
        if (memberService.validPassword(password, validPassword))
            return 1;
        else
            return 0;
    }
}
