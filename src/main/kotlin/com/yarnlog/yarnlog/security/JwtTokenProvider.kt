package com.yarnlog.yarnlog.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.Date

@Component
class JwtTokenProvider (
    @Value("\${jwt.secret}")
    secretKey: String,

    // 토큰 만료 시간 설정
    @Value("\${jwt.access-token-expiration-minutes}")
    private val accessTokenExpirationMinutes: Long
){
    private val key: Key = Keys.hmacShaKeyFor(secretKey.toByteArray(StandardCharsets.UTF_8))

    // 로그인 성공하면 아이디, 이메일 받아서 jwt 문자열 반환
    fun generateAccessToken(userId: Long, email: String): String{
        val now = Date()
        // 토큰 만료 시간 계산
        val validity = Date(now.time + accessTokenExpirationMinutes * 60_000)

        return Jwts.builder()
            .setSubject(userId.toString()) // 토큰 주체
            .claim("email", email) // 부가 정보
            .setIssuedAt(now) // 발급시간
            .setExpiration(validity) // 만료시간
            .signWith(key, SignatureAlgorithm.HS256) // 사용 알고리즘
            .compact() // jwt 문자열 생성
    }

    //  토큰에서 아이디 추출
    fun getUserId(token: String): Long{
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

        return claims.subject.toLong()
    }

    // 토큰 유효성 검사
    fun validateToken(token: String): Boolean{
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
            !claims.body.expiration.before(Date())
        }catch (ex: Exception){
            false
        }
    }
}