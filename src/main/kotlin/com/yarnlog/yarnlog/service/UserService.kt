package com.yarnlog.yarnlog.service

import com.yarnlog.yarnlog.domain.User
import com.yarnlog.yarnlog.dto.LoginRequest
import com.yarnlog.yarnlog.dto.LoginResponse
import com.yarnlog.yarnlog.dto.UserResponse
import com.yarnlog.yarnlog.repository.UserRepository
import com.yarnlog.yarnlog.dto.UserSignUpRequest
import com.yarnlog.yarnlog.security.JwtTokenProvider
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
){
    // 회원가입
    @Transactional
    fun signUp(request: UserSignUpRequest): UserResponse{
        // 1. 이메일 중복 체크
        if(userRepository.findByEmail(request.email) != null){
            throw IllegalArgumentException("이미 존재하는 이메일입니다.")
        }

        // 2. 비밀번호 암호화
        val encrytedPassword = passwordEncoder.encode(request.password)

        // 3. User 엔티티 생성
        val user = User(
            email = request.email,
            password = encrytedPassword,
            nickname = request.nickname
        )

        // 4. 가입 정보 저장
        val saved = userRepository.save(user)

        // 5. 응답 DTO로 변환
        return UserResponse(
            id = saved.id,
            email = saved.email,
            nickname = saved.nickname
        )
    }

    @Transactional(readOnly = true)
    fun login(request: LoginRequest): LoginResponse{
        // 1. 이메일 확인
        val user = userRepository.findByEmail(request.email)?: throw IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.")
        // 2. 비밀번호 확인
        if(!passwordEncoder.matches(request.password, user.password)){
            throw IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.")
        }

        val token = jwtTokenProvider.generateAccessToken(user.id, user.email)

        return LoginResponse(accessToken = token)
    }
}