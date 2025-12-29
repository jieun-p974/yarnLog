package com.yarnlog.yarnlog.repository

import com.yarnlog.yarnlog.domain.Yarn
import org.springframework.data.jpa.repository.JpaRepository

interface YarnRepository : JpaRepository<Yarn, Long>{
    fun findAllByUser_Id(userId: Long): List<Yarn>
}