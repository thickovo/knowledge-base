package com.gao.knowledgebase.config;

import com.gao.knowledgebase.utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtUtils jwtUtils) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers("/api/user/login","/api/user/register")
                .permitAll()
                .anyRequest().authenticated();
        http.addFilterBefore(new JwtAuthenticationFilter(jwtUtils),
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
