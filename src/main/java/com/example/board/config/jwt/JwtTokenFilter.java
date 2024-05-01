package com.example.board.config.jwt;

import com.example.board.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        //요청에서 jwt 추출하는 메서드
        String token = jwtProvider.resolveToken(req);

        try {
            //토큰이 빈값이 아니고 validateToken 메서드가 참일때
            if (token != null && jwtProvider.validateToken(token)) {

                Authentication auth = jwtProvider.getAuthentication(token); //추출된 토큰을 이용해 인증정보를 받아옴

                SecurityContextHolder.getContext().setAuthentication(auth); //securityContextHolder 에 현재 정보를 저장

            }
        } catch (CustomException e) {
            //현재 securityContextHolder 저장된값 지움
            SecurityContextHolder.clearContext();

            //response 객체에 에러코드와 메시지를 담음
            res.sendError(e.getErrorCode().getStatus().value(), e.getErrorCode().getMessage());

            log.error(e.getErrorCode() + "" + e.getErrorCode().getMessage());

            //메서드종료
            return;
        }

        //성공시 다음필터실행
        filterChain.doFilter(req, res);
    }
}
