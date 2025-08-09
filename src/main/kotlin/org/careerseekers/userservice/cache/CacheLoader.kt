package org.careerseekers.userservice.cache

import org.springframework.data.redis.core.RedisTemplate

interface CacheLoader<T> {
    val cacheKey: String
    val redisTemplate: RedisTemplate<String, T>

    fun getAllFromCache(): List<T> {
        val keys = redisTemplate.keys("$cacheKey::*")
        val ops = redisTemplate.opsForValue()

        return keys.mapNotNull { ops.get(it) }
    }

    fun preloadCache(): Any
    fun loadItemToCache(item: T): Any
}