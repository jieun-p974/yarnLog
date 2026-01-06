package com.yarnlog.yarnlog.repository

import com.yarnlog.yarnlog.domain.YarnTag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface YarnTagRepository : JpaRepository<YarnTag, Long> {

    fun findAllByYarn_Id(yarnId: Long): List<YarnTag>

    fun deleteAllByYarn_Id(yarnId: Long)

    @Query("""
        select yt
        from YarnTag yt
        join fetch yt.tag
        where yt.yarn.id in :yarnIds
    """)
    fun findAllWithTagByYarnIdIn(@Param("yarnIds") yarnIds: List<Long>): List<YarnTag>
}