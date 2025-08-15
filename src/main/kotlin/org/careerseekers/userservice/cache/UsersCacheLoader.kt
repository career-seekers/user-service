package org.careerseekers.userservice.cache

import org.careerseekers.userservice.dto.UsersCacheDto
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.cache.CacheManager
import org.springframework.context.event.EventListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class UsersCacheLoader(
    override val redisTemplate: RedisTemplate<String, UsersCacheDto>,
    cacheManager: CacheManager,
) : CacheLoader<UsersCacheDto>, CacheRetriever<UsersCacheDto>, CachePreloader {
    override val cacheKey = "users"
    private val cache = cacheManager.getCache(cacheKey)


    @EventListener(ApplicationReadyEvent::class)
    override fun preloadCache() {
        cache?.clear() ?: return
    }

    override fun loadItemToCache(item: UsersCacheDto) {
        cache?.putIfAbsent(item.id, item)
    }

    override fun getItemFromCache(key: Any): UsersCacheDto? {
        return cache?.get(key)?.let { it.get() as? UsersCacheDto }
    }
}