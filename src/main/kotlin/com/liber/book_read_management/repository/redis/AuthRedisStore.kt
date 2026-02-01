package com.liber.book_read_management.repository.redis

import org.springframework.stereotype.Repository

@Repository
class AuthRedisStore(
    private val redisTemplateRepository: RedisTemplateRepository
) {

    fun setValue(key: String, value: String) {
        redisTemplateRepository.setValue(key, value)
        redisTemplateRepository.expireKey(key, 600)
    }

    fun getValue(key: String) : String {
        return redisTemplateRepository.getValue(key).toString()
    }
}