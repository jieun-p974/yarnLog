package com.yarnlog.yarnlog.dto

data class TagSummaryResponse (
    val id: Long,
    val name: String,
    val slug: String,
    val yarnCount: Long // JPQL count 반환 타입 long이고 MySql Count도 bigint라서 long 타입 사용
)