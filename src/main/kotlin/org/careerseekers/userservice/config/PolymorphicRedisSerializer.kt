package org.careerseekers.userservice.config

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.springframework.data.redis.serializer.RedisSerializer

class PolymorphicRedisSerializer<Base : Any>(
    private val baseSerializer: KSerializer<Base>,
    private val json: Json
) : RedisSerializer<Base> {

    override fun serialize(t: Base?): ByteArray? {
        if (t == null) return null
        return json.encodeToString(baseSerializer, t).toByteArray(Charsets.UTF_8)
    }

    override fun deserialize(bytes: ByteArray?): Base? {
        if (bytes == null || bytes.isEmpty()) return null
        return json.decodeFromString(baseSerializer, bytes.toString(Charsets.UTF_8))
    }
}