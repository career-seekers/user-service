package org.careerseekers.userservice.services

import org.careerseekers.userservice.cache.TemporaryPasswordsCache
import org.careerseekers.userservice.dto.EmailSendingTaskDto
import org.careerseekers.userservice.dto.TemporaryPasswordDto
import org.careerseekers.userservice.dto.auth.LoginUserDto
import org.careerseekers.userservice.dto.auth.PreRegisterUserDto
import org.careerseekers.userservice.dto.auth.RegistrationDto
import org.careerseekers.userservice.dto.auth.UpdateUserTokensDto
import org.careerseekers.userservice.dto.jwt.CreateJwtToken
import org.careerseekers.userservice.dto.jwt.UserTokensDto
import org.careerseekers.userservice.dto.users.CreateUserDto
import org.careerseekers.userservice.enums.MailEventTypes
import org.careerseekers.userservice.exceptions.DoubleRecordException
import org.careerseekers.userservice.exceptions.JwtAuthenticationException
import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.careerseekers.userservice.services.kafka.producers.KafkaEmailSendingProducer
import org.careerseekers.userservice.services.processors.IUsersRegistrationProcessor
import org.careerseekers.userservice.utils.EmailVerificationCodeVerifier
import org.careerseekers.userservice.utils.JwtUtil
import org.careerseekers.userservice.utils.PasswordGenerator.generatePassword
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val jwtUtil: JwtUtil,
    private val usersService: UsersService,
    private val passwordEncoder: PasswordEncoder,
    private val temporaryPasswordsCache: TemporaryPasswordsCache,
    private val registrationPostProcessors: List<IUsersRegistrationProcessor>,
    private val emailSendingProducer: KafkaEmailSendingProducer,
    private val emailVerificationCodeVerifier: EmailVerificationCodeVerifier,
) {
    fun preRegister(item: PreRegisterUserDto): BasicSuccessfulResponse<String> {
        usersService.getByEmail(item.email, throwable = false)?.let {
            throw DoubleRecordException("User with email ${item.email} already exists")
        }

        usersService.getByMobileNumber(item.mobileNumber, throwable = false)?.let {
            throw DoubleRecordException("User with mobile number ${item.mobileNumber} already exists")
        }

        emailSendingProducer.sendMessage(EmailSendingTaskDto(
            email = item.email,
            eventType = MailEventTypes.PRE_REGISTRATION,
        ))

        return BasicSuccessfulResponse("Verification code sent to successfully")
    }

    @Transactional
    fun register(data: RegistrationDto): UserTokensDto {
        emailVerificationCodeVerifier.verify(
            email = data.email,
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
                password = data.password ?: generatePassword(data.email),
                role = data.role,
                avatarId = data.avatarId,
            )
        ).let {
            jwtUtil.removeOldRefreshTokenByUUID(data.uuid)
            UserTokensDto(
                jwtUtil.generateAccessToken(CreateJwtToken(it, data.uuid)),
                jwtUtil.generateRefreshToken(CreateJwtToken(it, data.uuid))
            )
        }.also {
            registrationPostProcessors.forEach { processor ->
                if (processor.userRole == data.role) {
                    processor.processRegistration(data)
                }
            }
        }
    }

    @Transactional
    fun login(data: LoginUserDto): UserTokensDto {
        return usersService.getByEmail(data.email, throwable = false)?.let {
            jwtUtil.removeOldRefreshTokenByUUID(data.uuid)
            if (passwordEncoder.matches(data.password, it.password)) {
                UserTokensDto(
                    jwtUtil.generateAccessToken(CreateJwtToken(it, data.uuid)),
                    jwtUtil.generateRefreshToken(CreateJwtToken(it, data.uuid))
                )
            } else throw JwtAuthenticationException("Wrong email or password")
        } ?: throw JwtAuthenticationException("Wrong email or password")
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
        } ?: throw JwtAuthenticationException("Invalid refresh token")
    }

    private fun generatePassword(email: String): String {
        val password = generatePassword()
        temporaryPasswordsCache.loadItemToCache(TemporaryPasswordDto(email, password))

        return passwordEncoder.encode(password)
    }
}