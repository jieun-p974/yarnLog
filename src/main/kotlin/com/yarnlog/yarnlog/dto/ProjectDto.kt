package com.yarnlog.yarnlog.dto

import java.time.LocalDate
import java.time.LocalDateTime

data class ProjectCreateRequest(
    val title: String,
    val memo: String?,
    val imageUrl: String?,
    val startedAt: LocalDate?,
    val finishedAt: LocalDate?,
    val yarnIds: List<Long>?,
    val tags: List<String>?
)

data class ProjectUpdateRequest(
    val title: String?,
    val memo: String?,
    val imageUrl: String?,
    val startedAt: LocalDate?,
    val finishedAt: LocalDate?,
    val yarnIds: List<Long>?,     // null: 변경 없음 / []: 전부 제거 / [..]: 교체
    val tags: List<String>?
)

data class ProjectResponse(
    val id: Long,
    val title: String,
    val memo: String?,
    val imageUrl: String?,
    val startedAt: LocalDate?,
    val finishedAt: LocalDate?,
    val yarns: List<YarnResponse>,
    val tags: List<TagResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
