package com.example.board.config;

import com.example.board.config.jwt.JwtFilterConfig;
import com.example.board.config.jwt.JwtProvider;
import com.example.board.config.jwt.JwtTokenFilter;
import com.example.board.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccountService accountService;
    private final JwtProvider jwtProvider;
    private final JwtTokenFilter jwtTokenFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable(); // csrf 해제
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 세션을 사용하지 않음
        http.apply(new JwtFilterConfig(jwtProvider)); // 필터 사용을 알림
        
        http.authorizeRequests()
                .antMatchers(/* swagger v2 */
                        "/v2/api-docs",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**",
                        /* swagger v3 */
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        /* 권한없이 사용가능한 url */
                        "/api/v1/member/login",
                        "/api/v1/member/register",
                        "/api/v1/board/list",
                        "/api/v1/board/detail/**",
                        "/api/v1/member/validEmail",
                        "/api/v1/member/validNickname",
                        "/main",
                        "/main.js",
                        "/favicon.ico")
                .permitAll() // 권한 없어도 접속가능
                .anyRequest().authenticated() // 나머지 url 은 검증된 사용자만 가능
                .and()
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class) // jwtTokenFilter 를 UsernamePasswordAuthenticationFilter 사용전에 사용
                .authenticationProvider(authenticationProvider()); //빈으로 등록해둔 provider

    }

    /*@Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/v2/api-docs",  "/configuration/ui",
                "/swagger-resources/**", "/configuration/security",
                "/swagger-ui.html", "/webjars/**","/swagger/**");
    }*/

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        //커스텀된 유저디테일스서비스를 등록
        authenticationProvider.setUserDetailsService(accountService);
        
        //빈으로 등록된 패스워드인코더 등록
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        
        return authenticationProvider;
    }

    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // AuthenticationManagerBuilder 에 커스텀된 userDetailsService 와 패스워드 인코더 저장
        auth.userDetailsService(accountService).passwordEncoder(passwordEncoder());
    }

    @Bean // 사용된 패스워드 인코더
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
