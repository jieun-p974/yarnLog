package com.yarnlog.yarnlog.repository

import com.yarnlog.yarnlog.domain.ProjectYarn
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectYarnRepository : JpaRepository<ProjectYarn, Long> {
    fun deleteAllByProject_Id(projectId: Long)
    fun findAllByProject_Id(projectId: Long): List<ProjectYarn>
}