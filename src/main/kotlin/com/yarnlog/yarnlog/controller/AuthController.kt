package com.yarnlog.yarnlog.controller

import com.yarnlog.yarnlog.dto.LoginRequest
import com.yarnlog.yarnlog.dto.LoginResponse
import com.yarnlog.yarnlog.dto.UserResponse
import com.yarnlog.yarnlog.dto.UserSignUpRequest
import com.yarnlog.yarnlog.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController (
    private val userService: UserService
){
    @PostMapping("/signup")
    fun signUp(
        @RequestBody request: UserSignUpRequest
    ): ResponseEntity<UserResponse>{
        val response = userService.signUp(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequest
    ): ResponseEntity<LoginResponse>{
        val response = userService.login(request)
        return ResponseEntity.ok(response)
    }
}