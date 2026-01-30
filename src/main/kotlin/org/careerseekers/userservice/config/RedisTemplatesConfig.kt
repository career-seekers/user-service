package org.careerseekers.userservice.config

import org.careerseekers.userservice.dto.CachesDto
import org.careerseekers.userservice.dto.TemporaryPasswordDto
import org.careerseekers.userservice.dto.UserAuthAttemptsDto
import org.careerseekers.userservice.dto.UsersCacheDto
import org.careerseekers.userservice.dto.VerificationCodeDto
import org.careerseekers.userservice.serializers.PolymorphicRedisSerializer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisTemplatesConfig(
    private val serializer: PolymorphicRedisSerializer<out CachesDto>
) {

    @Bean
    @Qualifier("users")
    fun usersRedisTemplate(
        connectionFactory: RedisConnectionFactory
    ): RedisTemplate<String, UsersCacheDto> {
        val template = RedisTemplate<String, UsersCacheDto>()
        template.connectionFactory = connectionFactory

        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = serializer

        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = serializer

        template.afterPropertiesSet()
        return template
    }

    @Bean
    @Qualifier("verificationCodes")
    fun verificationCodesRedisTemplate(
        connectionFactory: RedisConnectionFactory
    ): RedisTemplate<String, VerificationCodeDto> {
        val template = RedisTemplate<String, VerificationCodeDto>()
        template.connectionFactory = connectionFactory

        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = serializer

        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = serializer

        template.afterPropertiesSet()
        return template
    }

    @Bean
    @Qualifier("temporaryPasswords")
    fun temporaryPasswordsRedisTemplate(
        connectionFactory: RedisConnectionFactory
    ): RedisTemplate<String, TemporaryPasswordDto> {
        val template = RedisTemplate<String, TemporaryPasswordDto>()
        template.connectionFactory = connectionFactory

        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = serializer

        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = serializer

        template.afterPropertiesSet()
        return template
    }

    @Bean
    @Qualifier("userAuthAttempts")
    fun userAuthAttemptsRedisTemplate(
        connectionFactory: RedisConnectionFactory
    ): RedisTemplate<String, UserAuthAttemptsDto> {
        val template = RedisTemplate<String, UserAuthAttemptsDto>()
        template.connectionFactory = connectionFactory

        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = serializer

        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = serializer

        template.afterPropertiesSet()
        return template
    }
}