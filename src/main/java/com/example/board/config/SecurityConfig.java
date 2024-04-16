package com.example.board.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http
                .authorizeRequests()
                .antMatchers("/api/**/admin").hasRole("ADMIN")
                .antMatchers("/api/**/signUp").permitAll()
                .antMatchers("/swagger-ui.html",
                        "/swagger-ui/**", "/v2/api-docs",
                        "/v3/api-docs", "/swagger-resources/**").permitAll()
                .anyRequest().authenticated()
                .and()
                /*.authorizeRequests()
                .antMatchers("/admin").hasRole("ADMIN")
                .and()*/
                .formLogin().loginPage("/login").permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login").permitAll();
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
