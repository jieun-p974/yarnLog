package com.yarnlog.yarnlog.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "project_yarns",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_project_yarns_project_yarn", columnNames = ["project_id", "yarn_id"])
    ]
)
class ProjectYarn(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    val project: Project,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "yarn_id", nullable = false)
    val yarn: Yarn,

    @Column(name = "used_weight_gram")
    var usedWeightGram: Double? = null,

    @Column(name = "used_length")
    var usedLength: Int? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
