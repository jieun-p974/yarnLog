package com.yarnlog.yarnlog.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false, unique = true, length = 100)
    val email: String,

    @Column(nullable = false, length = 255)
    val password: String,

    @Column(length = 50)
    val nickname: String? = null,

    @Column(name = "created_at", nullable = false)
    // 가입한 날짜 시간 자동입력
    val createdAt: LocalDateTime = LocalDateTime.now()
)