package org.careerseekers.userservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.careerseekers.userservice.entities.JwtTokensStorage
import org.careerseekers.userservice.entities.Users
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisTemplatesConfig {
    @Bean
    @Qualifier("jwtTokensStorage")
    fun jwtStorageRedisTemplate(
        connectionFactory: RedisConnectionFactory
    ): RedisTemplate<String, JwtTokensStorage> {
        val template = RedisTemplate<String, JwtTokensStorage>()
        template.connectionFactory = connectionFactory

        val objectMapper = ObjectMapper()
            .registerKotlinModule()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

        val serializer = GenericJackson2JsonRedisSerializer(objectMapper)

        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = serializer

        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = serializer

        template.afterPropertiesSet()
        return template
    }

    @Bean
    @Qualifier("users")
    fun usersRedisTemplate(
        connectionFactory: RedisConnectionFactory
    ): RedisTemplate<String, Users> {
        val template = RedisTemplate<String, Users>()
        template.connectionFactory = connectionFactory

        val objectMapper = ObjectMapper()
            .registerKotlinModule()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

        val serializer = GenericJackson2JsonRedisSerializer(objectMapper)

        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = serializer

        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = serializer

        template.afterPropertiesSet()
        return template
    }
}