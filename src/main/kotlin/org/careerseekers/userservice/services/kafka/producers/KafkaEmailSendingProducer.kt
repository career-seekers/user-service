package org.careerseekers.userservice.services.kafka.producers

import org.careerseekers.userservice.dto.EmailSendingTaskDto
import org.careerseekers.userservice.enums.KafkaTopics
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaEmailSendingProducer(
    override val template: KafkaTemplate<String, EmailSendingTaskDto>,
) : CustomKafkaProducer<EmailSendingTaskDto> {
    override val topic = KafkaTopics.EMAIL_SENDING_TASKS

    override fun sendMessage(message: EmailSendingTaskDto) {
        template.send(topic.name, message)
    }
}