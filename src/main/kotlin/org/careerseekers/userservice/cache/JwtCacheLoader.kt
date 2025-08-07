package org.careerseekers.userservice.cache

import org.careerseekers.userservice.repositories.JwtTokensRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.cache.CacheManager
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class JwtCacheLoader(
    private val jwtTokensRepository: JwtTokensRepository,
    private val cacheManager: CacheManager
) : CacheLoader {

    @EventListener(ApplicationReadyEvent::class)
    override fun preloadCache() {
        val cache = cacheManager.getCache("jwtTokens") ?: return

        jwtTokensRepository.findAll().forEach { token ->
            cache.put(token.uuid.toString(), token)
        }
    }
}