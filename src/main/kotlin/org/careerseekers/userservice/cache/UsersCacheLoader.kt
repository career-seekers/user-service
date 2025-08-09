package org.careerseekers.userservice.cache

import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.services.UsersService
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.cache.CacheManager
import org.springframework.context.event.EventListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class UsersCacheLoader(
    override val redisTemplate: RedisTemplate<String, Users>,
    private val usersService: UsersService,
    cacheManager: CacheManager,
) : CacheLoader<Users> {
    override val cacheKey = "users"
    private val cache = cacheManager.getCache(cacheKey)


    @EventListener(ApplicationReadyEvent::class)
    override fun preloadCache() {
        cache?.clear() ?: return

        usersService.getAll().forEach { user -> cache.put(user.id, user) }
    }

    override fun loadItemToCache(user: Users) {
        cache?.putIfAbsent(user.id, user)
    }
}