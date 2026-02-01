package com.liber.book_read_management.repository.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import kotlin.collections.forEach

@Repository
class RedisTemplateRepositoryImpl (val redisTemplate: RedisTemplate<String, Any>) : RedisTemplateRepository {
    override fun setValue(key: String, value: String) {
        redisTemplate.opsForValue().set(key, value)
    }

    override fun setValueIfAbsent(key: String, value: String): Boolean {
        return redisTemplate.opsForValue().setIfAbsent(key, value) ?: false
    }

    override fun delKey(key: String): Boolean {
        return redisTemplate.delete(key)
    }

    override fun getValue(key: String): Any? {
        return redisTemplate.opsForValue().get(key)
    }

    override fun expireKey(key: String, seconds: Long): Boolean {
        return redisTemplate.expire(key, seconds, java.util.concurrent.TimeUnit.SECONDS)
    }

    override fun incrementValue(key: String, delta: Long): Long? {
        return redisTemplate.opsForValue().increment(key, delta)
    }

    override fun setZValue(key: String, value: String, score: Double): Boolean {
        return redisTemplate.opsForZSet().add(key, value, score) ?: false
    }

    override fun popZMinValue(key: String): Any? {
        return redisTemplate.opsForZSet().popMin(key)
    }

    override fun popZMaxValue(key: String): Any? {
        return redisTemplate.opsForZSet().popMax(key)
    }

    override fun getZRange(
        key: String,
        start: Long,
        end: Long
    ): Set<Any?>? {
        return redisTemplate.opsForZSet().range(key, start, end)
    }

    override fun getZRangeByScores(
        key: String,
        min: Double,
        max: Double
    ): Set<Any?>? {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max)
    }

    override fun getZRevRange(
        key: String,
        start: Long,
        end: Long
    ): Set<Any?>? {
        return redisTemplate.opsForZSet().reverseRange(key, start, end)
    }

    override fun getZRevRangeWithScores(
        key: String,
        start: Long,
        end: Long
    ): Map<Any, Double?> {
        val typedTupleSet = redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end)
        val resultMap = mutableMapOf<Any, Double?>()
        typedTupleSet?.forEach {
            resultMap[it.value!!] = it.score
        }
        return resultMap
    }


}