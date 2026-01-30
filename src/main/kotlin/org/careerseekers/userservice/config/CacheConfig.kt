package org.careerseekers.userservice.config

import org.careerseekers.userservice.dto.CachesDto
import org.careerseekers.userservice.serializers.PolymorphicRedisSerializer
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.RedisSerializationContext
import java.time.Duration

@Configuration
class CacheConfig(
    private val serializer: PolymorphicRedisSerializer<out CachesDto>
) {

    @Bean
    fun cacheConfiguration30Min(): RedisCacheConfiguration {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
            .disableCachingNullValues()
    }

    @Bean
    fun cacheConfiguration15Min(): RedisCacheConfiguration {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(15))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
            .disableCachingNullValues()
    }

    @Bean
    fun cacheManager(connectionFactory: RedisConnectionFactory): CacheManager {
        return RedisCacheManager.builder(connectionFactory)
            .withCacheConfiguration("userAuthAttempts", cacheConfiguration15Min())
            .cacheDefaults(cacheConfiguration30Min())
            .build()
    }
}