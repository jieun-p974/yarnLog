package com.yarnlog.yarnlog.controller

import com.yarnlog.yarnlog.dto.YarnCreateRequest
import com.yarnlog.yarnlog.dto.YarnResponse
import com.yarnlog.yarnlog.security.JwtTokenProvider
import com.yarnlog.yarnlog.service.YarnService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/yarns")
class YarnController (
    private val yarnService: YarnService,
    private val jwtTokenProvider: JwtTokenProvider
){
    @PostMapping
    fun createYarn(
        @RequestHeader("Authorization") authHeader: String,
        @RequestBody request: YarnCreateRequest
    ): ResponseEntity<YarnResponse>{
        val token = authHeader.removePrefix("Bearer ").trim()
        val userId = jwtTokenProvider.getUserId(token)
        val response = yarnService.createYarn(userId, request)

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}