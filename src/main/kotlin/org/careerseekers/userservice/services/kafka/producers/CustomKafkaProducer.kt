package org.careerseekers.userservice.services.kafka.producers

import org.careerseekers.userservice.dto.KafkaMessagesDto
import org.careerseekers.userservice.enums.KafkaTopics
import org.springframework.kafka.core.KafkaTemplate

interface CustomKafkaProducer<T : KafkaMessagesDto> {
    val topic: KafkaTopics
    val template: KafkaTemplate<String, T>

    fun sendMessage(message: T) {
        template.send(topic.name, message)
    }
}