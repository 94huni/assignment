package com.example.board.config.jwt;

import lombok.AllArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AllArgsConstructor
public class JwtFilterConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final JwtProvider jwtProvider;

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        JwtTokenFilter filter = new JwtTokenFilter(jwtProvider);

        //UsernamePasswordAuthenticationFilter 실행전 가로채서 JwtTokenFilter 를 먼저 실행시킴
        builder.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
