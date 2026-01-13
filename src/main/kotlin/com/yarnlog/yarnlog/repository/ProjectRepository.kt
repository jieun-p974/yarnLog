package com.yarnlog.yarnlog.repository

import com.yarnlog.yarnlog.domain.Project
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProjectRepository : JpaRepository<Project, Long>{
    // 사용자별 프로젝트
    fun findAllByUser_IdOrderByUpdatedAtDesc(userId: Long): List<Project>

    // 태그로 검색
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

    // 실로 프로젝트 검색
    @Query("""
        select distinct p 
        from Project p
        join ProjectYarn py on py.project = p
        join Yarn y on py.yarn = y
        where p.user.id = :userId
            and y.user.id = :userId
            and y.id = :yarnId
        order by p.updatedAt desc
    """)
    fun findAllByUserIdAndYarnIdOrderByUpdatedAtDesc(
        @Param("userId") userId: Long,
        @Param("yarnId") yarnId: Long
    ): List<Project>

    // 태그랑 실로 검색
    @Query("""
        select distinct p
        from Project p
        join ProjectYarn py on py.project = p
        join Yarn y on py.yarn = y
        join ProjectTag pt on pt.project = p
        join Tag t on pt.tag = t
        where p.user.id = :userId
          and y.user.id = :userId
          and y.id = :yarnId
          and t.slug = :slug
        order by p.updatedAt desc
    """)
    fun searchByUserIdWithTagAndYarn(
        @Param("userId") userId: Long,
        @Param("slug") slug: String,
        @Param("yarnId") yarnId: Long
    ): List<Project>

    // 키워드로 검색
    @Query("""
        select p
        from Project p
        where p.user.id = :userId
            and (
                lower(p.title) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(p.memo, '')) like lower(concat('%', :keyword, '%'))
            )
        order by p.updatedAt desc
    """)
    fun searchByUserIdWithKeyword(
        @Param("userId") userId: Long,
        @Param("keyword") keyword: String
    ): List<Project>

    // 태그, 키워드로 검색
    @Query("""
        select distinct p
        from Project p
        join ProjectTag pt on pt.project = p
        join Tag t on pt.tag = t
        where p.user.id = :userId
          and t.slug = :slug
          and (
            lower(p.title) like lower(concat('%', :keyword, '%'))
            or lower(coalesce(p.memo, '')) like lower(concat('%', :keyword, '%'))
          )
        order by p.updatedAt desc
    """)
    fun searchByUserIdWithTagAndKeyword(
        @Param("userId") userId: Long,
        @Param("slug") slug: String,
        @Param("keyword") keyword: String
    ): List<Project>

    // 실, 키워드 검색
    @Query("""
        select distinct p
        from Project p
        join ProjectYarn py on py.project = p
        join Yarn y on py.yarn = y
        where p.user.id = :userId
            and y.user.id =: userId
            and y.id = :yarnId
            and(
                lower(p.title) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(p.memo, '')) like lower(concat('%', :keyword, '%'))
            )
        order by p.updatedAt desc
    """)
    fun searchByUserIdWithYarnAndKeyword(
        @Param("userId") userId: Long,
        @Param("yarnId") yarnId: Long,
        @Param("keyword") keyword: String
    ): List<Project>

    // 태그, 실, 키워드로 검색
    @Query("""
        select distinct p
        from Project p
        join ProjectYarn py on py.project = p
        join Yarn y on py.yarn = y
        join ProjectTag pt on pt.project = p
        join Tag t on pt.tag = t
        where p.user.id = :userId
            and y.user.id = :userId
            and y.id = :yarnId
            and t.slug = :slug
            and (
                lower(p.title) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(p.memo, '')) like lower(concat('%', :keyword, '%'))
            )
        order by p.updatedAt desc
    """)
    fun searchByUserIdWithAllFilters(
        @Param("userId") userId: Long,
        @Param("slug") slug: String,
        @Param("yarnId") yarnId: Long,
        @Param("keyword") keyword: String
    ): List<Project>
}