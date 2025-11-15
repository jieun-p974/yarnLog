package com.yarnlog.yarnlog.repository

import com.yarnlog.yarnlog.domain.Yarn
import org.springframework.data.jpa.repository.JpaRepository

interface YarnRepository : JpaRepository<Yarn, Long>{
    fun findByUserId(userId: Long): List<Yarn>
}