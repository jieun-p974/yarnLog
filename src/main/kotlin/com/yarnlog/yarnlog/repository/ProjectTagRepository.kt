package com.yarnlog.yarnlog.repository

import com.yarnlog.yarnlog.domain.ProjectTag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProjectTagRepository: JpaRepository<ProjectTag, Long> {
    fun deleteAllByProject_Id(projectId: Long)
    fun findAllByProject_Id(projectId: Long): List<ProjectTag>
    @Query("""
        select pt
        from ProjectTag pt
        join fetch pt.tag
        where pt.project.id = :projectId
    """)
    fun findAllWithTagByProjectId(@Param("projectId") projectId: Long): List<ProjectTag>
    @Query("""
        select pt
        from ProjectTag pt
        join fetch pt.tag
        where pt.project.id in :projectIds
    """)
    fun findAllWithTagByProjectIdIn(@Param("projectIds") projectIds: List<Long>): List<ProjectTag>
}