package org.careerseekers.userservice.cache

import org.springframework.data.redis.core.RedisTemplate

interface CacheLoader<T> {
    val cacheKey: String
    val redisTemplate: RedisTemplate<String, T>

    fun loadItemToCache(item: T): Any
}