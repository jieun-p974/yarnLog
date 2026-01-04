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
    val imageUrl: String?
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
    val imageUrl: String?
)

// 엔티티 → 응답 DTO 변환
fun Yarn.toResponse(): YarnResponse =
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
        createdAt = createdAt,
        updatedAt = updatedAt
    )