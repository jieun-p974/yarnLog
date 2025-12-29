package com.yarnlog.yarnlog.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val customUserDetailsService: UserDetailsService
) : OncePerRequestFilter() {
    // 요청 들어올때마다 토큰 유효성 체크
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")
        // 헤더가 있고 Bearer로 시작하면 이후 문자열 잘라서 토큰에 저장
        val token = if(header != null && header.startsWith("Bearer ")){
            header.substring(7)
        }else{
            null
        }

        // 토큰 있으면 검증
        if (token != null && jwtTokenProvider.validateToken(token)){
            val userId = jwtTokenProvider.getUserId(token)
            // 실제 유저 정보 조회
            val userDetails: UserDetails = customUserDetailsService.loadUserByUsername(userId.toString())
            val authentication = UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.authorities
            )
            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

            SecurityContextHolder.getContext().authentication = authentication
        }
        filterChain.doFilter(request, response)
    }
}