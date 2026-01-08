package com.yarnlog.yarnlog.domain

import jakarta.persistence.*
import java.time.LocalDateTime

// 중간 테이블
@Entity
@Table(
    name = "project_tags",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_project_tags_project_tag", columnNames = ["project_id", "tag_id"])
    ]
)
class ProjectTag(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    val project: Project,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    val tag: Tag,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
