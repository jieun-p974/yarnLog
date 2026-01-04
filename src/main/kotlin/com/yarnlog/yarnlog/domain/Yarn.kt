package com.yarnlog.yarnlog.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "yarns")
class Yarn (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(length = 100)
    var name: String, // 실 이름

    @Column(length = 50)
    var brand: String? = null,

    @Column(name = "color_name",length = 50)
    var colorName: String? = null,

    @Column(name = "color_code",length = 7)
    var colorCode: String? = null, // #FFFFFF 형태

    @Column(name = "weight_gram")
    var weightGram: Double? = null, // 남은 중량 그람수

    @Column(name = "remaining_length")
    var remainingLength: Int? = null,

    @Column(columnDefinition = "TEXT")
    var memo: String? = null,

    @Column(name = "image_url", length = 255)
    var imageUrl: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)