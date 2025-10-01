package org.careerseekers.userservice.services.processors

import org.careerseekers.userservice.dto.EmailSendingTaskDto
import org.careerseekers.userservice.dto.auth.UserRegistrationDto
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.enums.MailEventTypes
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.io.converters.extensions.toCache
import org.careerseekers.userservice.services.UsersService
import org.careerseekers.userservice.services.kafka.producers.KafkaEmailSendingProducer
import org.springframework.stereotype.Service

@Service
class MentorRegistrationProcessor(
    private val usersService: UsersService,
    private val emailSendingProducer: KafkaEmailSendingProducer,
) : IUsersRegistrationProcessor, IUserNotificationProcessor {
    override val userRole = UsersRoles.MENTOR

    override fun processRegistration(item: UserRegistrationDto) {
        notifyUser(usersService.getByEmail(item.email)!!)
    }

    override fun notifyUser(user: Users) {
        emailSendingProducer.sendMessage(
            EmailSendingTaskDto(
                token = null,
                eventType = MailEventTypes.MENTOR_AND_USER_REGISTRATION,
                user = user.toCache(),
            )
        )
    }
}