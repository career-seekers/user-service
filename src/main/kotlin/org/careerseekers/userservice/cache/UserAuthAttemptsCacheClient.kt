package org.careerseekers.userservice.cache

import org.careerseekers.userservice.dto.UserAuthAttemptsDto
import org.springframework.cache.CacheManager
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class UserAuthAttemptsCacheClient(
    override val redisTemplate: RedisTemplate<String, UserAuthAttemptsDto>,
    cacheManager: CacheManager,
) : CacheLoader<UserAuthAttemptsDto>, CacheRetriever<UserAuthAttemptsDto> {

    override val cacheKey = "userAuthAttempts"
    private val cache = cacheManager.getCache(cacheKey)

    override fun loadItemToCache(item: UserAuthAttemptsDto) {
        cache?.put(item.email, item)
    }

    override fun getItemFromCache(key: Any): UserAuthAttemptsDto? {
        return cache?.get(key)?.let { it.get() as? UserAuthAttemptsDto }
    }

    fun incrementAttempts(key: String) {
        getItemFromCache(key)?.let { item ->
            loadItemToCache(item.copy(attempt = item.attempt + 1))
        }
    }
}