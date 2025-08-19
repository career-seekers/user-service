package org.careerseekers.userservice.services.processors

import org.careerseekers.userservice.dto.EmailSendingTaskDto
import org.careerseekers.userservice.dto.auth.RegisterUserDto
import org.careerseekers.userservice.dto.auth.RegisterUserExternalDto
import org.careerseekers.userservice.dto.auth.RegistrationDto
import org.careerseekers.userservice.dto.users.CreateChildDto
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.enums.MailEventTypes
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.exceptions.BadRequestException
import org.careerseekers.userservice.io.converters.extensions.toCache
import org.careerseekers.userservice.mappers.ChildrenMapper
import org.careerseekers.userservice.repositories.ChildrenRepository
import org.careerseekers.userservice.repositories.UsersRepository
import org.careerseekers.userservice.services.UsersService
import org.careerseekers.userservice.services.kafka.producers.KafkaEmailSendingProducer
import org.springframework.stereotype.Service

@Service
class UserRegistrationProcessor(
    private val childrenRepository: ChildrenRepository,
    private val childrenMapper: ChildrenMapper,
    private val usersService: UsersService,
    private val usersRepository: UsersRepository,
    private val emailSendingProducer: KafkaEmailSendingProducer,
) : IUsersRegistrationProcessor, IUserNotificationProcessor {
    override val userRole = UsersRoles.USER

    override fun <T : RegistrationDto> processRegistration(item: T) {
        when (item) {
            is RegisterUserDto -> processUserRegistration()
            is RegisterUserExternalDto -> processUserWithChildRegistration(item)
        }
    }

    private fun processUserRegistration(): Nothing =
        throw BadRequestException("Invalid data package for user registration")


    private fun processUserWithChildRegistration(item: RegisterUserExternalDto) {
        val user = usersService.getByEmail(item.email)!!
        val mentor = item.mentorId?.let { usersService.getById(it) }

        CreateChildDto(
            lastName = item.lastName,
            firstName = item.firstName,
            patronymic = item.patronymic,
            user = user,
            mentor = mentor
        ).let(childrenMapper::childFromDto)
            .run(childrenRepository::save)

        if (item.mentorEqualsUser) {
            user.isMentor = true
            usersRepository.save(user)

            notifyUser(user)
        }
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