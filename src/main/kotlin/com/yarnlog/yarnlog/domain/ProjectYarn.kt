package com.yarnlog.yarnlog.domain

import jakarta.persistence.*

@Entity
@Table(name = "project_yarns")
class ProjectYarn ( // 중간 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    val project: Project,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "yarn_id", nullable = false)
    val yarn: Yarn,

    @Column(name = "used_gram") // 사용한 실 그람수
    val usedGram: Int? = null
)