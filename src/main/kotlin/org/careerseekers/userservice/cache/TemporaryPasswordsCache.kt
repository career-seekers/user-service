package org.careerseekers.userservice.cache

import org.careerseekers.userservice.dto.TemporaryPasswordDto
import org.springframework.cache.CacheManager
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class TemporaryPasswordsCache(
    override val redisTemplate: RedisTemplate<String, TemporaryPasswordDto>,
    cacheManager: CacheManager,
) : CacheLoader<TemporaryPasswordDto> {
    override val cacheKey = "temporaryPasswords"
    private val cache = cacheManager.getCache(cacheKey)

    override fun loadItemToCache(item: TemporaryPasswordDto) {
        cache?.put(item.email, item)
    }
}