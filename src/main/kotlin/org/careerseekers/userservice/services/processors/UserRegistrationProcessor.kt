package org.careerseekers.userservice.services.processors

import org.careerseekers.userservice.dto.EmailSendingTaskDto
import org.careerseekers.userservice.dto.auth.UserRegistrationDto
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.enums.MailEventTypes
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.io.converters.extensions.toCache
import org.careerseekers.userservice.repositories.UsersRepository
import org.careerseekers.userservice.services.UsersService
import org.careerseekers.userservice.services.kafka.producers.KafkaEmailSendingProducer
import org.springframework.stereotype.Service

@Service
class UserRegistrationProcessor(
    private val usersService: UsersService,
    private val usersRepository: UsersRepository,
    private val emailSendingProducer: KafkaEmailSendingProducer,
) : IUsersRegistrationProcessor, IUserNotificationProcessor {
    override val userRole = UsersRoles.USER

    override fun processRegistration(item: UserRegistrationDto) {
        val user = usersService.getByEmail(item.email)!!

        if (item.mentorEqualsUser == true) {
            user.isMentor = true

            usersRepository.save(user)
        }
        notifyUser(user)
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