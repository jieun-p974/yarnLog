package com.yarnlog.yarnlog.service

import com.yarnlog.yarnlog.dto.TagSummaryResponse
import com.yarnlog.yarnlog.repository.TagRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TagService (
    private val tagRepository: TagRepository
){
    @Transactional(readOnly = true)
    fun getMyTags(userId: Long): List<TagSummaryResponse>{
        return tagRepository.findTagSummariesByUserId(userId)
    }

    @Transactional(readOnly = true)
    fun suggestMyTags(userId: Long, query: String, limit: Int = 10): List<TagSummaryResponse>{
        if(query.isBlank()) return emptyList()

        val results = tagRepository.findTagSuggestionsByUserId(userId, query.trim())

        return results.take(limit.coerceIn(1,50))
    }
}