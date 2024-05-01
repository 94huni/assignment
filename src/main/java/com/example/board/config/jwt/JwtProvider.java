package com.example.board.config.jwt;

import com.example.board.data.entity.Role;
import com.example.board.exception.CustomException;
import com.example.board.exception.ErrorCode;
import com.example.board.service.AccountService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {
    private final AccountService accountService;

    @Value("${spring.security.jwt.secret}")
    private String secretKey; // yml 파일에 저장한 secretKey 불러오기

    @Value("${spring.security.jwt.expiration}")
    private long validityInMilliseconds; // yml 파일에 저장한 만료시간 불러오기

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String email, Role role) {
        // Claims 에 email, 권한정보를 담음
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("auth", role);

        Date now = new Date();
        Date valid = new Date(now.getTime() + validityInMilliseconds); //현재시간 부터 yml에 저장해둔 시간후 만료

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, secretKey) // HS256 인코딩
                .setHeaderParam("typ", "JWT") // jwt header 저장
                .setClaims(claims) // email 과 권한 정보
                .setIssuedAt(now) // 발행시간
                .setExpiration(valid) // 만료시간
                .compact(); //생성
    }

    public String getUsername(String token) {
        log.info("Get User Name Token : " + token);
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject(); // 받아온 jwt 를 디코딩하여 Email 값을 가져옴
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = accountService.loadUserByUsername(getUsername(token)); // UserDetailsService 로그인 사용
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities()); // UsernamePasswordAuthenticationToken 에 현재 로그인정보 권한정보
    }

    public String resolveToken(HttpServletRequest request) {
        //필터에서 사용되는 메서드 Authorization 헤더안에 저장된 jwt 추출을 위해
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer "))
            return bearerToken.substring(7); // 토큰이 있고 스킴 Bearer 로 시작시 스킴을 제거하고 토큰만 반환

        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token); // jwt 디코딩
            log.info("validate Token : " + token);
            return true; // 성공시 참 반환
        } catch (JwtException | IllegalArgumentException e) {
            // jwt 익셉션 IllegalArgument 익셉션 발생시 403 반환
            log.error("Service Error : " + e.getMessage());
            throw new CustomException(ErrorCode.NOT_FORBIDDEN_MEMBER);
        }
    }
}
