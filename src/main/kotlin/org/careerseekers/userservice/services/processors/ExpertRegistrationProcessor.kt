package org.careerseekers.userservice.services.processors

import org.careerseekers.userservice.dto.EmailSendingTaskDto
import org.careerseekers.userservice.dto.auth.RegistrationDto
import org.careerseekers.userservice.enums.MailEventTypes
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.io.converters.extensions.toCache
import org.careerseekers.userservice.services.UsersService
import org.careerseekers.userservice.services.kafka.producers.KafkaEmailSendingProducer
import org.springframework.stereotype.Service

@Service
class ExpertRegistrationProcessor(
    private val emailSendingProducer: KafkaEmailSendingProducer,
    private val usersService: UsersService,
) : IUsersRegistrationProcessor {
    override val userRole = UsersRoles.EXPERT

    override fun <T : RegistrationDto> processRegistration(item: T) {
        usersService.getByEmail(item.email)?.let { user ->
            emailSendingProducer.sendMessage(EmailSendingTaskDto(
                user = user.toCache(),
                eventType = MailEventTypes.EXPERT_REGISTRATION,
            ))
        }
    }
}