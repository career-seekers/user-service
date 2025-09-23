package org.careerseekers.userservice.services

import org.careerseekers.userservice.cache.TemporaryPasswordsCache
import org.careerseekers.userservice.cache.VerificationCodesCacheClient
import org.careerseekers.userservice.dto.EmailSendingTaskDto
import org.careerseekers.userservice.dto.TemporaryPasswordDto
import org.careerseekers.userservice.dto.users.ChangePasswordSecondStepDto
import org.careerseekers.userservice.dto.users.ChangeUserRoleDto
import org.careerseekers.userservice.dto.users.CreateUserDto
import org.careerseekers.userservice.dto.users.UpdateUserDto
import org.careerseekers.userservice.dto.users.VerifyUserDto
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.enums.MailEventTypes
import org.careerseekers.userservice.enums.ReviewStatus
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.exceptions.BadRequestException
import org.careerseekers.userservice.exceptions.DoubleRecordException
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.io.converters.extensions.toCache
import org.careerseekers.userservice.mappers.UsersMapper
import org.careerseekers.userservice.repositories.UsersRepository
import org.careerseekers.userservice.services.interfaces.CrudService
import org.careerseekers.userservice.services.kafka.producers.KafkaEmailSendingProducer
import org.careerseekers.userservice.utils.EmailVerificationCodeVerifier
import org.careerseekers.userservice.utils.JwtUtil
import org.careerseekers.userservice.utils.MobileNumberFormatter.checkMobileNumberValid
import org.careerseekers.userservice.utils.PasswordGenerator
import org.careerseekers.userservice.utils.Tested
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Tested(testedBy = "scobca", createdOn = "21.08.2025", reviewStatus = ReviewStatus.APPROVED)
class UsersService(
    override val repository: UsersRepository,
    private val jwtUtil: JwtUtil,
    private val usersMapper: UsersMapper,
    private val passwordEncoder: PasswordEncoder,
    private val emailSendingProducer: KafkaEmailSendingProducer,
    private val temporaryPasswordsCache: TemporaryPasswordsCache,
    private val verificationCodesCacheClient: VerificationCodesCacheClient,
    private val emailVerificationCodeVerifier: EmailVerificationCodeVerifier,
    @param:Lazy private val usersService: UsersService?,
) : CrudService<Users, Long, CreateUserDto, UpdateUserDto> {

    @Value("\${file-service.default-avatar-id}")
    private lateinit var defaultAvatarId: String

    fun getByEmail(email: String, throwable: Boolean = true): Users? {
        return repository.getByEmail(email)
            ?: if (throwable) throw NotFoundException("Пользователь с адресом электронной почты $email не найден.") else null
    }

    fun getByMobileNumber(mobileNumber: String, throwable: Boolean = true): Users? {
        return repository.getByMobileNumber(mobileNumber)
            ?: if (throwable) throw NotFoundException("Пользователь с номером мобильного телефона $mobileNumber не найден.") else null
    }

    fun getByRole(role: UsersRoles): List<Users> = repository.getByRole(role)

    fun getByTutorId(tutorId: Long): List<Users> = repository.getByTutorId(tutorId)


    @Transactional
    override fun create(item: CreateUserDto): Users {
        item.email = item.email.lowercase()

        checkIfUserExistsByEmailOrMobile(item.email, item.mobileNumber)
        checkMobileNumberValid(item.mobileNumber)

        if (item.role == UsersRoles.EXPERT) {
            item.password = generatePassword(item.email)
            if (item.tutorId == null) throw BadRequestException("Эксперт должен быть связан с куратором.")
        } else {
            if (item.password == null) throw BadRequestException("Все пользователи должны быть созданы с использованием пароля.")
        }

        val userToSave = usersMapper.usersFromCreateDto(
            item.copy(
                password = passwordEncoder.encode(item.password),
                avatarId = defaultAvatarId.toLongOrNull()
            ),
        )
        return repository.save(userToSave).also { user ->
            if (user.role == UsersRoles.EXPERT) {
                emailSendingProducer.sendMessage(
                    EmailSendingTaskDto(
                        user = user.toCache(),
                        eventType = MailEventTypes.EXPERT_REGISTRATION,
                    )
                )
            }
        }
    }

    @Transactional
    override fun createAll(items: List<CreateUserDto>) {
        items.forEach { checkIfUserExistsByEmailOrMobile(it.email, it.mobileNumber) }
        items.forEach { checkMobileNumberValid(it.mobileNumber) }

        val usersToSave = items.map {
            usersMapper.usersFromCreateDto(
                it.copy(password = passwordEncoder.encode(it.password))
            )
        }
        repository.saveAll(usersToSave)
    }

    @Transactional
    override fun update(item: UpdateUserDto): String {
        fun require(value: Boolean, lazyMessage: () -> Any = { "Exception." }) {
            if (!value) throw BadRequestException(lazyMessage().toString())
        }

        val user = usersService?.getById(item.id, message = "Пользователь с ID ${item.id} не существует.")!!

        item.firstName?.let {
            require(it.isNotBlank()) { "Имя не должно быть пустым." }
            user.firstName = it
        }
        item.lastName?.let {
            require(it.isNotBlank()) { "Фамилия не должна быть пустой." }
            user.lastName = it
        }
        item.patronymic?.let {
            require(it.isNotBlank()) { "Отчество не должно быть пустым." }
            user.patronymic = it
        }
        item.email?.let {
            require(it.isNotBlank()) { "Адрес электронной почты не должен быть пустым." }
            if (item.email != user.email) {
                getByEmail(
                    it.lowercase().trim(),
                    throwable = false
                )?.let { user -> throw DoubleRecordException("Пользователь с электронной почтой ${user.email} уже существует.") }

                user.email = it
            }
        }
        item.mobileNumber?.let {
            require(it.isNotBlank()) { "Номер мобильного телефона не должен быть пустым." }
            if (item.mobileNumber != user.mobileNumber) {
                getByMobileNumber(
                    it.trim(),
                    throwable = false
                )?.let { user -> throw DoubleRecordException("Пользователь с номером мобильного телефона $it уже существует.") }
                user.mobileNumber = it
            }
        }
        item.dateOfBirth?.let { user.dateOfBirth = it }
        item.tutorId?.let { user.tutorId = it }

        return "Информация о пользователе обновлена успешно."
    }

    fun changePasswordFirstStep(jwtToken: String): String {
        emailSendingProducer.sendMessage(
            EmailSendingTaskDto(
                token = jwtToken,
                eventType = MailEventTypes.PASSWORD_RESET
            )
        )

        return "Электронное письмо успешно отправлено."
    }

    @Transactional
    fun updateRole(item: ChangeUserRoleDto): String {
        getById(item.id, message = "Пользователь с ID ${item.id} не найден.")!!.apply {
            role = item.role
        }.also(repository::save)

        return "Роль пользователя была успешно обновлена до ${item.role}."
    }

    @Transactional
    fun changePasswordSecondStep(item: ChangePasswordSecondStepDto, jwtToken: String): String {
        val user = jwtUtil.getUserFromToken(jwtToken) ?: throw NotFoundException("User not found")

        emailVerificationCodeVerifier.verify(
            email = user.email,
            verificationCode = item.verificationCode,
            token = jwtToken,
            mailEventTypes = MailEventTypes.PASSWORD_RESET,
        )

        user.password = passwordEncoder.encode(item.newPassword)
        repository.save(user)

        verificationCodesCacheClient.deleteItemFromCache(user.id)

        return "Информация о пользователе обновлена успешно."
    }

    @Transactional
    fun verifyUser(item: VerifyUserDto): String {
        usersService?.getById(item.userId, message = "Пользователь с ID ${item.userId} не найден.").let {
            it?.verified = item.status
        }
        return "Подтверждение пользователя успешно обновлено."
    }

    @Transactional
    override fun deleteById(id: Long): String {
        usersService?.getById(id, message = "Пользователь с ID $id не найден.")?.let { user ->
            repository.delete(user)
        }

        return "Пользователь удалён успешно."
    }

    @Transactional
    override fun deleteAll(): String {
        super.deleteAll()

        return "Все пользователи удалены успешно."
    }

    private fun checkIfUserExistsByEmailOrMobile(email: String, mobile: String) {
        if (usersService?.getByEmail(email, false) != null) {
            throw DoubleRecordException("Пользователь с адресом электронной почты $email уже существует.")
        }
        if (usersService?.getByMobileNumber(mobile, false) != null) {
            throw DoubleRecordException("Пользователь с номером мобильного телефона $mobile уже существует.")
        }
    }

    private fun generatePassword(email: String): String {
        val password = PasswordGenerator.generatePassword()
        temporaryPasswordsCache.loadItemToCache(TemporaryPasswordDto(email, password))

        return password
    }
}