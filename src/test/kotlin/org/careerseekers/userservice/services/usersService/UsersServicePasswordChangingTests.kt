package org.careerseekers.userservice.services.usersService

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.cache.VerificationCodesCacheClient
import org.careerseekers.userservice.dto.EmailSendingTaskDto
import org.careerseekers.userservice.enums.MailEventTypes
import org.careerseekers.userservice.mappers.UsersMapper
import org.careerseekers.userservice.repositories.UsersRepository
import org.careerseekers.userservice.services.UsersService
import org.careerseekers.userservice.services.kafka.producers.KafkaEmailSendingProducer
import org.careerseekers.userservice.utils.DocumentExistenceChecker
import org.careerseekers.userservice.utils.EmailVerificationCodeVerifier
import org.careerseekers.userservice.utils.JwtUtil
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockKExtension::class)
class UsersServicePasswordChangingTests {
    private val repository = mockk<UsersRepository>()
    private val jwtUtil = mockk<JwtUtil>()
    private val usersMapper = mockk<UsersMapper>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val emailSendingProducer = mockk<KafkaEmailSendingProducer>()
    private val verificationCodesCacheClient = mockk<VerificationCodesCacheClient>()
    private val documentExistenceChecker = mockk<DocumentExistenceChecker>()
    private val emailVerificationCodeVerifier = mockk<EmailVerificationCodeVerifier>()

    private val usersServiceMock = mockk<UsersService>(relaxed = true)
    private val serviceUnderTest = UsersService(
        repository,
        jwtUtil,
        usersMapper,
        passwordEncoder,
        emailSendingProducer,
        verificationCodesCacheClient,
        documentExistenceChecker,
        emailVerificationCodeVerifier,
        usersServiceMock
    )

    @Nested
    inner class ChangePasswordFirstStep {
        @Test
        fun `changePasswordFirstStep should send email with password reset event`() {
            val jwtToken = "dummyJwtToken"

            every { emailSendingProducer.sendMessage(any()) } returns Unit

            val result = serviceUnderTest.changePasswordFirstStep(jwtToken)

            verify(exactly = 1) {
                emailSendingProducer.sendMessage(match {
                    it.token == jwtToken && it.eventType == MailEventTypes.PASSWORD_RESET
                })
            }

            assertThat(result).isEqualTo("Email sent successfully")
        }
    }
}