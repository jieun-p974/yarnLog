package com.yarnlog.yarnlog.repository

import com.yarnlog.yarnlog.domain.Yarn
import org.springframework.data.jpa.repository.JpaRepository

interface YarnRepository : JpaRepository<Yarn, Long>{
    fun findAllByUser_Id(userId: Long): List<Yarn>
    // 로그인한 유저의 실 목록을 최근 수정 순으로 조회
    fun findAllByUser_IdOrderByUpdatedAtDesc(userId: Long): List<Yarn>
}