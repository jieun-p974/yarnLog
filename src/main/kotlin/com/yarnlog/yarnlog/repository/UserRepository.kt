package com.yarnlog.yarnlog.repository

import com.yarnlog.yarnlog.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>{
    fun findByEmail(email: String): User?
}