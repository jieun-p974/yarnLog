package com.yarnlog.yarnlog.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "tags",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_tags_slug", columnNames = ["slug"])
    ]
)
class Tag (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(length = 50, nullable = false)
    var name: String,

    @Column(length = 50, nullable = false)
    var slug: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)