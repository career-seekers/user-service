package org.careerseekers.userservice.dto.mail

import org.careerseekers.userservice.dto.UniversalEmailMessageDto
import org.careerseekers.userservice.services.kafka.producers.KafkaUniversalEmailMessagesProducer
import org.springframework.stereotype.Service

@Service
class MailerService(private val producer: KafkaUniversalEmailMessagesProducer) {

    fun sendEmail(item: UniversalEmailMessageDto): String {
        producer.sendMessage(item)

        return "Письмо отправлено."
    }
}