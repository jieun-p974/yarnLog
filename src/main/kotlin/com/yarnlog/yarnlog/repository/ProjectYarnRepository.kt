package com.yarnlog.yarnlog.repository

import com.yarnlog.yarnlog.domain.ProjectYarn
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProjectYarnRepository : JpaRepository<ProjectYarn, Long> {
    fun deleteAllByProject_Id(projectId: Long)
    fun findAllByProject_Id(projectId: Long): List<ProjectYarn>
    @Query("""
        select py
        from ProjectYarn py
        where py.project.id in :projectIds
    """)
    fun findAllByProjectIdIn(@Param("projectIds") projectId: List<Long>): List<ProjectYarn>
}