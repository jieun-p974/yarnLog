package com.yarnlog.yarnlog.service

import com.yarnlog.yarnlog.domain.Yarn
import com.yarnlog.yarnlog.dto.YarnCreateRequest
import com.yarnlog.yarnlog.dto.YarnResponse
import com.yarnlog.yarnlog.dto.toResponse
import com.yarnlog.yarnlog.repository.UserRepository
import com.yarnlog.yarnlog.repository.YarnRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class YarnService (
    private val yarnRepository: YarnRepository,
    private val userRepository: UserRepository
){
    @Transactional
    fun createYarn(userId: Long, request: YarnCreateRequest): YarnResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }

        val yarn = Yarn(
            user = user,
            name = request.name,
            brand = request.brand,
            colorName = request.colorName,
            colorCode = request.colorCode,
            weightGram = request.weightGram,
            remainingLength = request.remainingLength,
            memo = request.memo,
            imageUrl = request.imageUrl
            )

        val saved = yarnRepository.save(yarn)

        return saved.toResponse()
    }
}