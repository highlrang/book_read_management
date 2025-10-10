package com.liber.read_log_api.repository

import com.liber.read_log_api.entities.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {

    fun findByLoginId(loginId: String) : User?

}