package org.careerseekers.userservice.services

import org.careerseekers.userservice.annotations.Tested
import org.careerseekers.userservice.cache.VerificationCodesCacheClient
import org.careerseekers.userservice.dto.EmailSendingTaskDto
import org.careerseekers.userservice.dto.auth.*
import org.careerseekers.userservice.dto.jwt.CreateJwtToken
import org.careerseekers.userservice.dto.jwt.UserTokensDto
import org.careerseekers.userservice.dto.users.CreateUserDto
import org.careerseekers.userservice.enums.MailEventTypes
import org.careerseekers.userservice.enums.ReviewStatus
import org.careerseekers.userservice.exceptions.BadRequestException
import org.careerseekers.userservice.exceptions.DoubleRecordException
import org.careerseekers.userservice.exceptions.JwtAuthenticationException
import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.careerseekers.userservice.io.converters.extensions.toCache
import org.careerseekers.userservice.repositories.UsersRepository
import org.careerseekers.userservice.security.JwtUtil
import org.careerseekers.userservice.services.kafka.producers.KafkaEmailSendingProducer
import org.careerseekers.userservice.services.processors.IUsersRegistrationProcessor
import org.careerseekers.userservice.utils.validators.EmailVerificationCodeVerifier
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Tested(testedBy = "scobca", createdOn = "22.08.2025", reviewStatus = ReviewStatus.APPROVED)
class AuthService(
    private val jwtUtil: JwtUtil,
    private val usersService: UsersService,
    private val passwordEncoder: PasswordEncoder,
    private val usersRepository: UsersRepository,
    private val emailSendingProducer: KafkaEmailSendingProducer,
    private val verificationCodesCacheClient: VerificationCodesCacheClient,
    private val emailVerificationCodeVerifier: EmailVerificationCodeVerifier,
    private val registrationPostProcessors: List<IUsersRegistrationProcessor>,
) {
    fun preRegister(item: PreRegisterUserDto): BasicSuccessfulResponse<String> {
        item.email = item.email.lowercase()
        usersService.getByEmail(item.email, throwable = false)?.let {
            throw DoubleRecordException("Пользователь с адресом электронной почты ${item.email} уже существует.")
        }

        emailSendingProducer.sendMessage(
            EmailSendingTaskDto(
                email = item.email,
                eventType = MailEventTypes.PRE_REGISTRATION,
            )
        )

        return BasicSuccessfulResponse("Проверочный код успешно отправлен.")
    }

    @Transactional
    fun register(data: UserRegistrationDto): UserTokensDto {
        emailVerificationCodeVerifier.verify(
            email = data.email.lowercase(),
            verificationCode = data.verificationCode,
            mailEventTypes = MailEventTypes.PRE_REGISTRATION,
        )

        return usersService.create(
            CreateUserDto(
                firstName = data.firstName,
                lastName = data.lastName,
                patronymic = data.patronymic,
                dateOfBirth = data.dateOfBirth,
                email = data.email,
                mobileNumber = data.mobileNumber,
                password = data.password,
                role = data.role,
            )
        ).let {
            jwtUtil.removeOldRefreshTokenByUUID(data.uuid)
            UserTokensDto(
                jwtUtil.generateAccessToken(CreateJwtToken(it, data.uuid)),
                jwtUtil.generateRefreshToken(CreateJwtToken(it, data.uuid))
            )
        }.also {
            verificationCodesCacheClient.deleteItemFromCache(data.email)
            registrationPostProcessors.forEach { processor ->
                if (processor.userRole == data.role) {
                    processor.processRegistration(data)
                }
            }
        }
    }

    @Transactional
    fun login(data: LoginUserDto): UserTokensDto {
        val user = usersService.getByEmail(data.email.lowercase())!!
        jwtUtil.removeOldRefreshTokenByUUID(data.uuid)

        return if (passwordEncoder.matches(data.password, user.password)) {
            UserTokensDto(
                jwtUtil.generateAccessToken(CreateJwtToken(user, data.uuid)),
                jwtUtil.generateRefreshToken(CreateJwtToken(user, data.uuid))
            )
        } else throw JwtAuthenticationException("Неверный адрес электронной почты или пароль.")

    }

    @Transactional
    fun updateTokens(data: UpdateUserTokensDto): UserTokensDto {
        jwtUtil.verifyToken(data.accessToken, throwTimeLimit = false)
        jwtUtil.verifyToken(data.refreshToken, data.uuid)
        jwtUtil.removeOldRefreshTokenByUUID(data.uuid)

        return jwtUtil.getUserFromToken(data.refreshToken)?.run {
            UserTokensDto(
                jwtUtil.generateAccessToken(CreateJwtToken(this, data.uuid)),
                jwtUtil.generateRefreshToken(CreateJwtToken(this, data.uuid))
            )
        } ?: throw JwtAuthenticationException("Неверный refresh-токен.")
    }

    fun forgotPassword(item: ForgotPasswordDto): String {
        emailSendingProducer.sendMessage(
            EmailSendingTaskDto(
                email = item.email,
                user = usersService.getByEmail(item.email)?.toCache(),
                eventType = MailEventTypes.PASSWORD_RESET
            )
        )

        return "Электронное письмо успешно отправлено."
    }

    fun verifyCode(item: CodeVerificationDto): String {
        val user = usersService.getByEmail(item.email)!!

        emailVerificationCodeVerifier.verify(
            email = user.email,
            verificationCode = item.code,
            user = user.toCache(),
            mailEventTypes = MailEventTypes.PASSWORD_RESET,
        )


        return "Код подтверждён."
    }

    fun resetPassword(item: ResetPasswordDto): String {
        val user = usersService.getByEmail(item.email)!!

        emailVerificationCodeVerifier.verify(
            email = user.email,
            verificationCode = item.code,
            user = user.toCache(),
            mailEventTypes = MailEventTypes.PASSWORD_RESET,
        )

        if (item.newPassword != item.confirmPassword) {
            throw BadRequestException("Введенные пароли не совпадают, попробуйте еще раз.")
        }

        usersService.getByEmail(item.email)!!.apply {
            password = passwordEncoder.encode(item.newPassword)
        }.also(usersRepository::save)

        verificationCodesCacheClient.deleteItemFromCache(item.email)

        return "Пароль обновлён успешно."
    }
}