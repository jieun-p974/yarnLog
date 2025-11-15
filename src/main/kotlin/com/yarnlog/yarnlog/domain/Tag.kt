package com.yarnlog.yarnlog.domain

import jakarta.persistence.*

@Entity
@Table(name = "tags")
class Tag (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(length = 50, nullable = false)
    val name: String
)