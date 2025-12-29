package com.yarnlog.yarnlog.dto

data class UserSignUpRequest (
    val email: String,
    val password: String,
    val nickname: String?
)

data class UserResponse(
    val id: Long,
    val email: String,
    val nickname: String?
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val accessToken: String
)