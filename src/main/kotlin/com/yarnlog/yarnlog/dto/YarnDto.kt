package com.yarnlog.yarnlog.dto

import com.yarnlog.yarnlog.domain.Yarn
import java.time.LocalDateTime

data class YarnCreateRequest (
    val name: String,
    val brand: String?,
    val colorName: String?,
    val colorCode: String?,
    val remainingLength: Int?, // 남은 실 길이
    val weightGram: Double?, // 남은 실 무게(g)
    val memo: String?,
    val imageUrl: String?,
    val tags: List<String>? // 태그 이름 리스트
)

data class YarnResponse (
    val id: Long,
    val name: String,
    val brand: String?,
    val colorName: String?,
    val colorCode: String?,
    val remainingLength: Int?,
    val weightGram: Double?,
    val memo: String?,
    val imageUrl: String?,
    val tags: List<TagResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class YarnUpdateRequest(
    val name: String?,
    val brand: String?,
    val colorName: String?,
    val colorCode: String?,
    val weightGram: Double?,
    val remainingLength: Int?,
    val memo: String?,
    val imageUrl: String?,
    val tags: List<String>? // 수정 시 태그 교체
)

data class TagResponse(
    val id: Long,
    val name: String,
    val slug: String
)

// 엔티티 → 응답 DTO 변환
fun Yarn.toResponse(tags: List<TagResponse>): YarnResponse =
    YarnResponse(
        id = id,
        name = name,
        brand = brand,
        colorName = colorName,
        colorCode = colorCode,
        weightGram = weightGram,
        remainingLength = remainingLength,
        memo = memo,
        imageUrl = imageUrl,
        tags = tags,
        createdAt = createdAt,
        updatedAt = updatedAt
    )