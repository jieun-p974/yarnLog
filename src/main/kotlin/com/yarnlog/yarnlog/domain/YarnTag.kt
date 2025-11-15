package com.yarnlog.yarnlog.domain

import jakarta.persistence.*


@Entity
@Table(name = "yarn_tags") // 중간 테이블
class YarnTag (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "yarn_id", nullable = false)
    val yarn: Yarn,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    val tag: Tag
)