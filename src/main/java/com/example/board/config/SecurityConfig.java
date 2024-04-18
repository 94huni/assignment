package com.example.board.config;

import com.example.board.config.jwt.JwtFilterConfig;
import com.example.board.config.jwt.JwtProvider;
import com.example.board.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccountService accountService;
    private final JwtProvider jwtProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.apply(new JwtFilterConfig(jwtProvider));
//        http.antMatcher("/api/**")
//                .authorizeRequests()
//                .antMatchers("/api/**/admin").hasRole("ADMIN")
//                .antMatchers("/api/**/signUp").permitAll()
//                .antMatchers("/swagger-ui.html",
//                        "/swagger-ui/**", "/v2/api-docs",
//                        "/v3/api-docs", "/swagger-resources/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                /*.authorizeRequests()
//                .antMatchers("/admin").hasRole("ADMIN")
//                .and()*/
//                .formLogin().loginPage("/login").permitAll()
//                .and()
//                .logout()
//                .logoutUrl("/logout")
//                .logoutSuccessUrl("/login").permitAll();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2-docs", "/swagger-ui.html",
                        "/swagger-ui/**", "/v2/api-docs",
                        "/v3/api-docs", "/swagger-resources/**",
                        "/webjars", "/public");
    }

    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
