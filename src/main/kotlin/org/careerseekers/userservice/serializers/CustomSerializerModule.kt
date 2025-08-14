package org.careerseekers.userservice.serializers

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.careerseekers.userservice.dto.CachesDto
import org.careerseekers.userservice.dto.EmailSendingTask
import org.careerseekers.userservice.dto.KafkaMessagesDto
import org.careerseekers.userservice.dto.UsersCacheDto

object CustomSerializerModule {
    val customSerializerModule = SerializersModule {
        polymorphic(CachesDto::class) {
            subclass(UsersCacheDto::class, UsersCacheDto.serializer())
        }
        polymorphic(KafkaMessagesDto::class) {
            subclass(EmailSendingTask::class, EmailSendingTask.serializer())
        }
    }

    val json = Json {
        serializersModule = customSerializerModule
        classDiscriminator = "type"
        ignoreUnknownKeys = true
    }
}