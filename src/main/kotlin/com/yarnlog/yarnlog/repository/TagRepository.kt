package com.yarnlog.yarnlog.repository

import com.yarnlog.yarnlog.domain.Tag
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<Tag, Long>{
    // slug로 태그 조회
    fun findBySlug(slug: String): Tag?
}