package com.yarnlog.yarnlog.controller

import com.yarnlog.yarnlog.dto.YarnCreateRequest
import com.yarnlog.yarnlog.dto.YarnResponse
import com.yarnlog.yarnlog.dto.YarnUpdateRequest
import com.yarnlog.yarnlog.repository.YarnRepository
import com.yarnlog.yarnlog.security.JwtTokenProvider
import com.yarnlog.yarnlog.service.YarnService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

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

    // 실 목록 조회
    @GetMapping
    fun getYarns(
        @RequestHeader("Authorization") authHeader: String,
        @RequestParam(required = false) tag: String?
    ):ResponseEntity<List<YarnResponse>>{
        val token = authHeader.removePrefix("Bearer ").trim()
        val userId = jwtTokenProvider.getUserId(token)
        val response = yarnService.getYarns(userId, tag)

        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getYarn(
        @RequestHeader("Authorization") authHeader: String,
        @PathVariable id: Long
    ): ResponseEntity<YarnResponse>{
        val token = authHeader.removePrefix("Bearer ").trim()
        val userId = jwtTokenProvider.getUserId(token)
        val response = yarnService.getYarn(userId, id)

        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{id}")
    fun updateYarn(
        @RequestHeader("Authorization") authHeader: String,
        @PathVariable id: Long,
        @RequestBody request: YarnUpdateRequest
    ): ResponseEntity<YarnResponse>{
        val token = authHeader.removePrefix("Bearer ").trim()
        val userId = jwtTokenProvider.getUserId(token)
        val response = yarnService.updateYarn(userId, id, request)

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deleteYarn(
        @RequestHeader("Authorization") authHeader: String,
        @PathVariable id: Long
    ): ResponseEntity<Void>{
        val token = authHeader.removePrefix("Bearer ").trim()
        val userId = jwtTokenProvider.getUserId(token)

        yarnService.deleteYarn(userId, id)

        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/image")
    fun uploadYarnImage(
        @RequestHeader("Authorization") authHeader: String,
        @PathVariable id: Long,
        @RequestPart("file") file:MultipartFile
    ): ResponseEntity<YarnResponse>{
        val token = authHeader.removePrefix("Bearer ").trim()
        val userId = jwtTokenProvider.getUserId(token)
        val response = yarnService.uploadYarnImage(userId, id, file)

        return ResponseEntity.ok(response)
    }
}