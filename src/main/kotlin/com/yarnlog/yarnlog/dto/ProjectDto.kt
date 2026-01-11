package com.yarnlog.yarnlog.dto

import com.yarnlog.yarnlog.domain.Project
import java.time.LocalDate
import java.time.LocalDateTime

data class ProjectCreateRequest(
    val title: String,
    val memo: String? = null,
    val imageUrl: String? = null,
    val startedAt: LocalDate? = null,
    val finishedAt: LocalDate? = null,
    val yarnIds: List<Long>? = null,
    val tags: List<String>? = null
)

data class ProjectUpdateRequest(
    val title: String? = null,
    val memo: String? = null,
    val imageUrl: String? = null,
    val startedAt: LocalDate? = null,
    val finishedAt: LocalDate? = null,
    val yarnIds: List<Long>? = null,
    val tags: List<String>? = null
)

data class ProjectResponse(
    val id: Long,
    val title: String,
    val memo: String?,
    val imageUrl: String?,
    val startedAt: LocalDate?,
    val finishedAt: LocalDate?,
    val yarnIds: List<Long>?,
    val tags: List<TagResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

fun Project.toResponse(yarnIds: List<Long>, tags: List<TagResponse>): ProjectResponse =
    ProjectResponse(
        id = id,
        title = title,
        memo = memo,
        imageUrl = imageUrl,
        startedAt = startedAt,
        finishedAt = finishedAt,
        yarnIds = yarnIds,
        tags = tags,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

fun Project.toResponse(): ProjectResponse =
    toResponse(emptyList(), emptyList())