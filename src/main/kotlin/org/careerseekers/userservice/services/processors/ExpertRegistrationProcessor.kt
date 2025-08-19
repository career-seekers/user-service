package org.careerseekers.userservice.services.processors

import org.careerseekers.userservice.dto.EmailSendingTaskDto
import org.careerseekers.userservice.dto.auth.RegistrationDto
import org.careerseekers.userservice.enums.MailEventTypes
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.services.kafka.producers.KafkaEmailSendingProducer
import org.springframework.stereotype.Service

@Service
class ExpertRegistrationProcessor(
    private val emailSendingProducer: KafkaEmailSendingProducer,
) : IUsersRegistrationProcessor {
    override val userRole = UsersRoles.EXPERT

    override fun <T : RegistrationDto> processRegistration(item: T) {
        emailSendingProducer.sendMessage(EmailSendingTaskDto(
            email = item.email,
            eventType = MailEventTypes.EXPERT_REGISTRATION,
        ))
    }
}