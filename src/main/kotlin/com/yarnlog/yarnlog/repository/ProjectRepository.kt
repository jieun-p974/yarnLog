package com.yarnlog.yarnlog.repository

import com.yarnlog.yarnlog.domain.Project
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProjectRepository : JpaRepository<Project, Long>{
    fun findAllByIdInAndUser_Id(ids: List<Long>, userId: Long, sort: Sort): List<Project>

    @Query("select p.id from Project p where p.user.id = :userId")
    fun findProjectIdsByUserId(@Param("userId") userId: Long): List<Long>

    // 태그로 조회
    @Query("""
        select distinct p.id
        from Project p
        join ProjectTag pt on pt.project = p
        join Tag t on pt.tag = t
        where p.user.id = :userId and t.slug = :slug
    """)
    fun findProjectIdsByUserIdAndTagSlug(@Param("userId") userId: Long, @Param("slug") slug: String): List<Long>

    // 실로 조회
    @Query("""
    select distinct p.id
    from Project p
    join ProjectYarn py on py.project = p
    join Yarn y on py.yarn = y
    where p.user.id = :userId and y.user.id = :userId and y.id = :yarnId
""")
    fun findProjectIdsByUserIdAndYarnId(@Param("userId") userId: Long, @Param("yarnId") yarnId: Long): List<Long>

    // 키워드로 조회
    @Query("""
    select distinct p.id
    from Project p
    where p.user.id = :userId
      and (
        lower(p.title) like lower(concat('%', :keyword, '%'))
        or lower(coalesce(p.memo, '')) like lower(concat('%', :keyword, '%'))
      )
""")
    fun findProjectIdsByUserIdAndKeyword(@Param("userId") userId: Long, @Param("keyword") keyword: String): List<Long>
}