package com.yarnlog.yarnlog.util

import java.text.Normalizer
import java.util.*

object SlugUtil {
    // 태그 관리용 유틸
    // 아이보리 실 -> 아이보리-실
    fun toSlug(input: String): String{
        val trimmed = input.trim()
        if(trimmed.isBlank()) return ""

        val slug = trimmed.lowercase(Locale.getDefault())
            .replace(Regex("[^a-z0-9가-힣\\s-]"), "")
            .replace(Regex("\\s+"), "-")
            .replace(Regex("-+"), "-")
            .trim('-')

        return slug
    }
}