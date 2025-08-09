package org.careerseekers.userservice.cache

import org.careerseekers.userservice.entities.JwtTokensStorage
import org.careerseekers.userservice.repositories.JwtTokensRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.cache.CacheManager
import org.springframework.context.event.EventListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class JwtCacheLoader(
    override val redisTemplate: RedisTemplate<String, JwtTokensStorage>,
    private val jwtTokensRepository: JwtTokensRepository,
    cacheManager: CacheManager
) : CacheLoader<JwtTokensStorage> {
    override val cacheKey = "jwtTokens"
    private val cache = cacheManager.getCache(cacheKey)

    @EventListener(ApplicationReadyEvent::class)
    override fun preloadCache() {
        cache?.clear() ?: return

        jwtTokensRepository.findAll().forEach { token ->
            cache.put(token.uuid.toString(), token)
        }
    }

    override fun loadItemToCache(item: JwtTokensStorage) {
        cache?.putIfAbsent(item.uuid.toString(), item)
    }

    override fun getItemFromCache(key: Any): JwtTokensStorage? {
        return cache?.get(key)?.let { it as? JwtTokensStorage }
    }
}