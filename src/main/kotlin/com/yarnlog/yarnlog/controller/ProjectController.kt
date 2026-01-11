package com.yarnlog.yarnlog.controller

import com.yarnlog.yarnlog.dto.ProjectCreateRequest
import com.yarnlog.yarnlog.dto.ProjectResponse
import com.yarnlog.yarnlog.dto.ProjectUpdateRequest
import com.yarnlog.yarnlog.security.JwtTokenProvider
import com.yarnlog.yarnlog.service.ProjectService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/projects")
class ProjectController (
    private val projectService: ProjectService,
    private val jwtTokenProvider: JwtTokenProvider
){
    @PostMapping
    fun createProject(
        @RequestHeader("Authorization") authHeader: String,
        @RequestBody request: ProjectCreateRequest
    ): ResponseEntity<ProjectResponse>{
        val token = authHeader.removePrefix("Bearer ").trim()
        val userId = jwtTokenProvider.getUserId(token)
        val response = projectService.createProject(userId, request)

        return ResponseEntity.status(201).body(response)
    }

    @GetMapping
    fun getProjects(
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<List<ProjectResponse>>{
        val token = authHeader.removePrefix("Bearer ").trim()
        val userId = jwtTokenProvider.getUserId(token)
        val response = projectService.getProjects(userId)

        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getProject(
        @RequestHeader("Authorization") authHeader: String,
        @PathVariable id: Long
    ): ResponseEntity<ProjectResponse>{
        val token = authHeader.removePrefix("Bearer ").trim()
        val userId = jwtTokenProvider.getUserId(token)
        val response = projectService.getProject(userId, id)

        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{id}")
    fun updateProject(
        @RequestHeader("Authorization") authHeader: String,
        @PathVariable id: Long,
        @RequestBody request: ProjectUpdateRequest
    ): ResponseEntity<ProjectResponse>{
        val token = authHeader.removePrefix("Bearer ").trim()
        val userId = jwtTokenProvider.getUserId(token)
        val response = projectService.updateProject(userId, id, request)

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deleteProject(
        @RequestHeader("Authorization") authHeader: String,
        @PathVariable id: Long
    ): ResponseEntity<Void>{
        val token = authHeader.removePrefix("Bearer ").trim()
        val userId = jwtTokenProvider.getUserId(token)

        projectService.deleteProject(userId, id)

        return ResponseEntity.noContent().build()
    }
}