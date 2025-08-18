package org.careerseekers.userservice.serializers

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.careerseekers.userservice.dto.CachesDto
import org.careerseekers.userservice.dto.EmailSendingTaskDto
import org.careerseekers.userservice.dto.KafkaMessagesDto
import org.careerseekers.userservice.dto.UsersCacheDto
import org.careerseekers.userservice.dto.auth.RegisterUserDto
import org.careerseekers.userservice.dto.auth.RegisterUserExternalDto
import org.careerseekers.userservice.dto.auth.RegistrationDto

object CustomSerializerModule {
    val customSerializerModule = SerializersModule {
        polymorphic(CachesDto::class) {
            subclass(UsersCacheDto::class, UsersCacheDto.serializer())
        }
        polymorphic(KafkaMessagesDto::class) {
            subclass(EmailSendingTaskDto::class, EmailSendingTaskDto.serializer())
        }
        polymorphic(RegistrationDto::class) {
            subclass(RegisterUserDto::class, RegisterUserDto.serializer())
            subclass(RegisterUserExternalDto::class, RegisterUserExternalDto.serializer())
        }
    }

    val json = Json {
        serializersModule = customSerializerModule
        classDiscriminator = "type"
        ignoreUnknownKeys = true
    }
}