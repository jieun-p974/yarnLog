package com.yarnlog.yarnlog.repository

import com.yarnlog.yarnlog.domain.Project
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProjectRepository : JpaRepository<Project, Long>{
    fun findAllByUser_IdOrderByUpdatedAtDesc(userId: Long): List<Project>
    @Query("""
        select distinct p
        from Project p
        join ProjectTag pt on pt.project = p
        join Tag t on pt.tag = t
        where p.user.id = :userId
            and t.slug = :slug
        order by p.updatedAt desc
    """)
    fun findAllByUserIdAndTagSlugOrderByUpdatedAtDesc(
        @Param("userId") userId: Long,
        @Param("slug") slug: String
    ):List<Project>
}