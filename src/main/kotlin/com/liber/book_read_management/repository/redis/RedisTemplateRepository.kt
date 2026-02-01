package com.liber.book_read_management.repository.redis

interface RedisTemplateRepository {

    fun setValue(key: String, value: String)
    fun setValueIfAbsent(key: String, value: String) : Boolean
    fun delKey(key: String) : Boolean
    fun getValue(key: String) : Any?

    fun expireKey(key: String, seconds: Long) : Boolean

    fun incrementValue(key: String, delta: Long) : Long?

    fun setZValue(key: String, value: String, score: Double) : Boolean
    fun popZMinValue(key: String) : Any?
    fun popZMaxValue(key: String) : Any?
    fun getZRange(key: String, start: Long, end: Long): Set<Any?>?
    fun getZRangeByScores(key: String, min: Double, max: Double): Set<Any?>?
    fun getZRevRange(key: String, start: Long, end: Long) : Set<Any?>?
    fun getZRevRangeWithScores(key: String, start: Long, end: Long) : Map<Any, Double?>

}