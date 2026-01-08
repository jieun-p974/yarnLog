package com.yarnlog.yarnlog.repository

import com.yarnlog.yarnlog.domain.Project
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectRepository : JpaRepository<Project, Long>{
    fun findAllByUser_IdOrderByUpdatedAtDesc(userId: Long): List<Project>
}