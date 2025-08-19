package org.careerseekers.userservice.cache

import org.careerseekers.userservice.dto.VerificationCodeDto
import org.springframework.cache.CacheManager
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class VerificationCodesCacheClient(
    override val redisTemplate: RedisTemplate<String, VerificationCodeDto>,
    cacheManager: CacheManager,
) : CacheLoader<VerificationCodeDto>, CacheRetriever<VerificationCodeDto> {
    override val cacheKey = "verification_codes"
    private val cache = cacheManager.getCache(cacheKey)


    override fun getItemFromCache(key: Any): VerificationCodeDto? {
        return cache?.get(key)?.let { it.get() as? VerificationCodeDto }
    }

    fun deleteItemFromCache(key: Any) {
        cache?.evictIfPresent(key)
    }

    override fun loadItemToCache(item: VerificationCodeDto) {
        cache?.put(item.userEmail, item)
    }
}