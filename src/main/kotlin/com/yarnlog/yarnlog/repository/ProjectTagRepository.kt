package com.yarnlog.yarnlog.repository

import com.yarnlog.yarnlog.domain.ProjectTag
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectTagRepository: JpaRepository<ProjectTag, Long> {
    fun deleteAllByProject_Id(projectId: Long)
    fun findAllByProject_Id(projectId: Long): List<ProjectTag>
}