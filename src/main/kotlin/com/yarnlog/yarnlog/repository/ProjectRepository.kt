package com.yarnlog.yarnlog.repository

import com.yarnlog.yarnlog.domain.Project
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectRepository : JpaRepository<Project, Long>{
    fun findByUser_Id(userId: Long): List<Project>
}