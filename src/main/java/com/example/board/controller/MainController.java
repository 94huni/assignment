package com.example.board.controller;

import com.example.board.service.impl.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final MemberService memberService;

    @GetMapping("/main")
    public String main(HttpServletRequest request,
                       Model model) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    // JWT 토큰이 존재하면 로그인된 상태로 간주하고, 사용자 정보를 모델에 추가
                    String jwtToken = cookie.getValue();
                    String username = memberService.currentMember(jwtToken);

                    // 사용자 정보를 모델에 추가
                    model.addAttribute("nickName", username);

                    break;
                }
            }
        }
        return "main";
    }

}
