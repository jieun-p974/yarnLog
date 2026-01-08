package com.yarnlog.yarnlog.domain

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "projects")
class Project(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(length = 100, nullable = false)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var memo: String? = null,

    @Column(name = "image_url", length = 255)
    var imageUrl: String? = null,

    @Column(name = "started_at") // 시작일
    var startedAt: LocalDate? = null,

    @Column(name = "finished_at") // 완성일
    var finishedAt: LocalDate? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
