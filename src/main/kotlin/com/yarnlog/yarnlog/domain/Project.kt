package com.yarnlog.yarnlog.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "projects")
class Project (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(length = 100, nullable = false) // 프로젝트명
    val title: String,

    @Column(name = "pattern_name", length = 100) // 도안 명
    val patternName: String? = null,

    @Column(name = "gauge_text", length = 100) // 10x10cm, 4mm, 21sts/27rows 형태로 입력
    val gaugeText: String? = null,

    @Column(name = "needle_size", length = 50) // 바늘 사이즈
    val needleSize: String? = null,

    @Column(columnDefinition = "TEXT")
    val memo: String? = null,

    @Column(name = "image_url", length = 255)
    val imageUrl: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)