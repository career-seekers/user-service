package org.careerseekers.userservice.dto

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.careerseekers.userservice.enums.MailEventTypes

@Serializable
@Polymorphic
sealed class KafkaMessagesDto : DtoClass

@Serializable
@SerialName("email_sending_task")
class EmailSendingTaskDto(
    val email: String? = null,
    val token: String? = null,
    val user: UsersCacheDto? = null,
    val eventType: MailEventTypes,
) : KafkaMessagesDto()

@Serializable
@SerialName("tg_link_notification")
class TgLinkNotificationDto(
    val user: UsersCacheDto,
    val eventType: MailEventTypes = MailEventTypes.TG_LINK_CREATION,
) : KafkaMessagesDto()

@Serializable
@SerialName("UniversalEmailMessage")
data class UniversalEmailMessageDto(
    val email: String,
    val subject: String,
    val body: String,
) : KafkaMessagesDto()