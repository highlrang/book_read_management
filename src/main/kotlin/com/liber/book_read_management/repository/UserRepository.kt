package com.liber.book_read_management.repository

import com.liber.book_read_management.entities.User
import com.liber.book_read_management.enums.SocialProvider
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {

    fun findByEmail(email: String) : User?

    fun findBySocialProviderAndSocialId(socialProvider: SocialProvider, socialId: String) : User?

    fun existsByNickname(nickname: String) : Boolean

}
