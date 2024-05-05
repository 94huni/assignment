package com.example.board.controller.restController;

import com.example.board.data.entity.Member;
import com.example.board.data.requestDto.MemberSignUp;
import com.example.board.data.requestDto.MemberUpdate;
import com.example.board.data.requestDto.SignIn;
import com.example.board.data.responseDto.MemberResponse;
import com.example.board.service.impl.MemberService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
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
        // 멤버 서비스에서 로그인 요청에 성공하면 jwt 를 반환해줌
        String token = memberService.signIn(signIn);

        // 토큰을 json 반환을 위해 Map 에 저장
        Map<String, String> res = new HashMap<>();
        res.put("token", token);

        log.info("Member Token : {}", token);

        return ResponseEntity.ok(res); //post 요청이지만 로그인은 200 코드 사용
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> memberSignUp(@Valid @RequestBody MemberSignUp memberSignUp) {
        // 받아온 정보를 서비스로 보내서 회원가입
        memberService.signUpMember(memberSignUp);

        log.info("MemberEmail : {}", memberSignUp.getEmail());

        //반환 결과를 담아줄 Map
        Map<String, String> result = new HashMap<>();
        result.put("message", "Membership registration completed");

        return ResponseEntity.status(HttpStatus.CREATED).body(result); // 새로운 정보 생성을 했기 때문에 201 Created
    }

    @GetMapping("/info")
    public ResponseEntity<MemberResponse> memberInfo(Principal principal) {
        return ResponseEntity.ok(memberService.getMember(principal.getName()));
    }

    @PutMapping("/update")
    public ResponseEntity<String> memberUpdate(Principal principal,
                                               @Valid @RequestBody MemberUpdate memberUpdate) {

        //현재 로그인된 정보
        Member member = memberService.principalMember(principal);

        // 현재 로그인된 정보와 바꿀 정보를 바꿔줄 updateMember
        memberService.updateMember(memberUpdate, member);
        log.info("Current Member email : {}", member.getEmail());

        return ResponseEntity.ok("Successfully changed membership information");
    }

    @Hidden
    @PostMapping("/validNickname") // 회원가입시 중복 닉네임 방지
    public int validNickname(String nickname) {
        if (memberService.validNickname(nickname))
            return 0;
        else
            return 1;
    }

    @Hidden
    @PostMapping("/validEmail") // 회원가입시 중복 이메일 방지
    public int validEmail(String email) {
        if (memberService.validEmail(email))
            return 0;
        else
            return 1;
    }

}
