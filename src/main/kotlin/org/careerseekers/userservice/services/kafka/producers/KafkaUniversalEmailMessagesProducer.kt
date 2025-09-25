package org.careerseekers.userservice.services.kafka.producers

import org.careerseekers.userservice.dto.UniversalEmailMessageDto
import org.careerseekers.userservice.enums.KafkaTopics
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaUniversalEmailMessagesProducer(
    override val template: KafkaTemplate<String, UniversalEmailMessageDto>
) : CustomKafkaProducer<UniversalEmailMessageDto> {
    override val topic = KafkaTopics.UNIVERSAL_EMAIL_MESSAGES_TOPIC
}