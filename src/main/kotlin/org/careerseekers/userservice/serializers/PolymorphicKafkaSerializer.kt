package org.careerseekers.userservice.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serializer

class PolymorphicKafkaSerializer<Base : Any>(
    private val baseSerializer: KSerializer<Base>,
    private val json: Json
) : Serializer<Base>, Deserializer<Base> {

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}

    override fun close() {}

    override fun deserialize(topic: String, data: ByteArray?): Base? {
        if (data == null || data.isEmpty()) return null
        return json.decodeFromString(baseSerializer, data.toString(Charsets.UTF_8))
    }

    override fun serialize(topic: String, data: Base?): ByteArray? {
        if (data == null) return null
        return json.encodeToString(baseSerializer, data).toByteArray(Charsets.UTF_8)
    }
}