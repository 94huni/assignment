package com.example.board.controller.restController;

import com.example.board.data.requestDto.MemberSignUp;
import com.example.board.data.requestDto.MemberUpdate;
import com.example.board.data.requestDto.SignIn;
import com.example.board.service.impl.MemberService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginMember(@RequestBody @Valid SignIn signIn) {
        String token = memberService.signIn(signIn);

        Map<String, String> res = new HashMap<>();
        res.put("token", token);
        // 유저이름 + 닉네임 추가 필요

        log.info("Member Token : {}", token);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/register")
    public ResponseEntity<String> memberSignUp(@Valid @RequestBody MemberSignUp memberSignUp) {
        memberService.signUpMember(memberSignUp);

        log.info("MemberEmail : {}", memberSignUp.getEmail());

        return ResponseEntity.ok("Membership registration completed");
    }

    @PutMapping("/update")
    public ResponseEntity<String> memberUpdate(HttpServletRequest request,
                                               @Valid @RequestBody MemberUpdate memberUpdate) {

        String token = (String) request.getAttribute("token");
        log.info(request.toString());

        String currentMember = memberService.currentMember(token);

        memberService.updateMember(memberUpdate, token);
        log.info("Current Member email : {}", currentMember);

        return ResponseEntity.ok("Successfully changed membership information");
    }

    @Hidden
    @GetMapping("/validNickname")
    public int validNickname(String nickname) {
        if (memberService.validNickname(nickname))
            return 1;
        else
            return 0;
    }

    @Hidden
    @GetMapping("/validEmail")
    public int validEmail(String email) {
        if (memberService.validEmail(email))
            return 1;
        else
            return 0;
    }

    @Hidden
    @GetMapping("/validPassword")
    public int validPassword(String password, String validPassword) {
        if (memberService.validPassword(password, validPassword))
            return 1;
        else
            return 0;
    }
}
