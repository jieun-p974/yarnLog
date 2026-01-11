package com.yarnlog.yarnlog.repository

import com.yarnlog.yarnlog.domain.Yarn
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface YarnRepository : JpaRepository<Yarn, Long>{
    fun findAllByUser_Id(userId: Long): List<Yarn>
    // 로그인한 유저의 실 목록을 최근 수정 순으로 조회
    fun findAllByUser_IdOrderByUpdatedAtDesc(userId: Long): List<Yarn>

    @Query("""
        select distinct y
        from Yarn y
        join YarnTag yt on yt.yarn = y
        join Tag t on yt.tag = t
        where y.user.id = :userId
          and t.slug = :slug
        order by y.updatedAt desc
    """)
    fun findAllByUserIdAndTagSlugOrderByUpdatedAtDesc(
        @Param("userId") userId: Long,
        @Param("slug") slug: String
    ): List<Yarn>

    fun findAllByIdInAndUser_Id(ids: List<Long>, userId: Long): List<Yarn>
}