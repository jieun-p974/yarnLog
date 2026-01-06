package com.yarnlog.yarnlog.domain

import jakarta.persistence.*
import java.time.LocalDateTime

// 중간 테이블
@Entity
@Table(
    name = "yarn_tags",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_yarn_tags_yarn_tag", columnNames = ["yarn_id", "tag_id"])
    ]
)
class YarnTag (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "yarn_id", nullable = false)
    val yarn: Yarn,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    val tag: Tag,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)