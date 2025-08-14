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
class EmailSendingTask(
    val token: String,
    val eventType: MailEventTypes,
) : KafkaMessagesDto()