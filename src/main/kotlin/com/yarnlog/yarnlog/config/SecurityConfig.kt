package com.yarnlog.yarnlog.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity):SecurityFilterChain{
        http.csrf{it.disable()}
            .sessionManagement{
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests{
                auth -> auth.requestMatchers(
                    // 회원가입, 로그인, 헬스체크 url 허용
                    "/api/auth/**",
                    "/api/health",
                    "/uploads/**"
                ).permitAll()
                .anyRequest()
                .permitAll()
            }
            .httpBasic{it.disable()}// 기본 로그인폼 막기

        return http.build()
    }
}