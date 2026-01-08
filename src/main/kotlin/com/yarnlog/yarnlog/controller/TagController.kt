package com.yarnlog.yarnlog.controller

import com.yarnlog.yarnlog.dto.TagSummaryResponse
import com.yarnlog.yarnlog.security.JwtTokenProvider
import com.yarnlog.yarnlog.service.TagService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/tags")
class TagController (
    private val tagService: TagService,
    private val jwtTokenProvider: JwtTokenProvider
){
    @GetMapping
    fun getMyTags(
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<List<TagSummaryResponse>>{
        val token = authHeader.removePrefix("Bearer ").trim()
        val userId = jwtTokenProvider.getUserId(token)

        return ResponseEntity.ok(tagService.getMyTags(userId))
    }

    @GetMapping("/suggest")
    fun suggestTags(
        @RequestHeader("Authorization") authHeader: String,
        @RequestParam query: String,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ):ResponseEntity<List<TagSummaryResponse>>{
        val token = authHeader.removePrefix("Bearer ").trim()
        val userId = jwtTokenProvider.getUserId(token)

        return ResponseEntity.ok(tagService.suggestMyTags(userId, query, limit))
    }
}