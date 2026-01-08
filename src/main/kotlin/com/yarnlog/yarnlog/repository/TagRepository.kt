package com.yarnlog.yarnlog.repository

import com.yarnlog.yarnlog.domain.Tag
import com.yarnlog.yarnlog.dto.TagSummaryResponse
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface TagRepository : JpaRepository<Tag, Long>{
    // slug로 태그 조회
    fun findBySlug(slug: String): Tag?

    // 태그 목록 집계
    @Query("""
        select new com.yarnlog.yarnlog.dto.TagSummaryResponse(
            t.id,
            t.name,
            t.slug,
            count(distinct y.id)
        )
        from Tag t
        join YarnTag yt on yt.tag = t
        join Yarn y on yt.yarn = y
        where y.user.id = :userId
        group by t.id, t.name, t.slug
        order by count(distinct y.id) desc, t.slug asc
    """)
    fun findTagSummariesByUserId(@Param("userId") userId: Long): List<TagSummaryResponse>

    // 태그 자동완성
    @Query("""
        select new com.yarnlog.yarnlog.dto.TagSummaryResponse(
            t.id,
            t.name,
            t.slug,
            count(distinct y.id)
        )
        from Tag t
        join YarnTag yt on yt.tag = t
        join Yarn y on yt.yarn = y
        where y.user.id = :userId
          and (
            lower(t.name) like lower(concat('%', :q, '%'))
            or lower(t.slug) like lower(concat('%', :q, '%'))
          )
        group by t.id, t.name, t.slug
        order by count(distinct y.id) desc, t.slug asc
    """)
    fun findTagSuggestionsByUserId(
        @Param("userId") userId: Long,
        @Param("q") q: String
    ): List<TagSummaryResponse>
}