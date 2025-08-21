package org.careerseekers.userservice.services.usersService

import io.mockk.Called
import io.mockk.Runs
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.UsersCreator.createUser
import org.careerseekers.userservice.cache.VerificationCodesCacheClient
import org.careerseekers.userservice.dto.users.ChangePasswordSecondStepDto
import org.careerseekers.userservice.enums.MailEventTypes
import org.careerseekers.userservice.exceptions.BadRequestException
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mappers.UsersMapper
import org.careerseekers.userservice.repositories.UsersRepository
import org.careerseekers.userservice.services.UsersService
import org.careerseekers.userservice.services.kafka.producers.KafkaEmailSendingProducer
import org.careerseekers.userservice.utils.DocumentExistenceChecker
import org.careerseekers.userservice.utils.EmailVerificationCodeVerifier
import org.careerseekers.userservice.utils.JwtUtil
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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

    @Nested
    inner class ChangePasswordSecondStep {
        @Test
        fun `changePasswordSecondStep should update password and clear cache`() {
            val jwtToken = "validToken"
            val item = ChangePasswordSecondStepDto(newPassword = "newPass", verificationCode = "code123")

            val user = createUser().copy(id = 1L, email = "user@example.com", password = "oldPass")

            every { jwtUtil.getUserFromToken(jwtToken) } returns user
            every {
                emailVerificationCodeVerifier.verify(
                    email = user.email,
                    verificationCode = item.verificationCode,
                    token = jwtToken,
                    mailEventTypes = MailEventTypes.PASSWORD_RESET
                )
            } just Runs
            every { passwordEncoder.encode(item.newPassword) } returns "encodedNewPass"
            every { repository.save(user) } returns user
            every { verificationCodesCacheClient.deleteItemFromCache(user.id) } just Runs

            val result = serviceUnderTest.changePasswordSecondStep(item, jwtToken)

            assertThat(result).isEqualTo("User updated successfully.")
            assertThat(user.password).isEqualTo("encodedNewPass")

            verifyOrder {
                jwtUtil.getUserFromToken(jwtToken)
                emailVerificationCodeVerifier.verify(
                    user.email,
                    item.verificationCode,
                    jwtToken,
                    MailEventTypes.PASSWORD_RESET
                )
                passwordEncoder.encode(item.newPassword)
                repository.save(user)
                verificationCodesCacheClient.deleteItemFromCache(user.id)
            }
        }

        @Test
        fun `changePasswordSecondStep should throw NotFoundException when user not found`() {
            val jwtToken = "invalidToken"
            val item = ChangePasswordSecondStepDto(newPassword = "newPass", verificationCode = "code123")

            every { jwtUtil.getUserFromToken(jwtToken) } returns null

            assertThrows<NotFoundException> {
                serviceUnderTest.changePasswordSecondStep(item, jwtToken)
            }

            verify { repository wasNot Called }
            verify { verificationCodesCacheClient wasNot Called }
        }

        @Test
        fun `verify should throw BadRequestException when code is incorrect and retries less than 3`() {
            val token = "dummyJwtToken"
            val email = "user@example.com"
            val user = createUser().copy(id = 1L, email = email)
            val item = ChangePasswordSecondStepDto(newPassword = "newPass", verificationCode = "code123")

            every { jwtUtil.getUserFromToken(token) } returns user
            every {
                emailVerificationCodeVerifier.verify(
                    email = email,
                    verificationCode = any(),
                    token = any(),
                    mailEventTypes = MailEventTypes.PASSWORD_RESET,
                )
            } throws BadRequestException("Incorrect verification code")
            every { passwordEncoder.encode(any()) } returns "encodedNewPass"
            every { repository.save(any()) } returns user
            every { verificationCodesCacheClient.deleteItemFromCache(any()) } just Runs

            val exception = assertThrows<BadRequestException> {
                serviceUnderTest.changePasswordSecondStep(item, token)
            }

            assertThat(exception.message).isEqualTo("Incorrect verification code")

            verify { jwtUtil.getUserFromToken(token) }
            verify {
                emailVerificationCodeVerifier.verify(
                    email = email,
                    verificationCode = any(),
                    token = any(),
                    mailEventTypes = MailEventTypes.PASSWORD_RESET,
                )
            }
            verify(exactly = 0) { passwordEncoder.encode(any()) }
            verify { repository wasNot Called }
            verify { verificationCodesCacheClient wasNot Called }
        }

        @Test
        fun `verify should throw BadRequestException when code is incorrect and retries more than 3`() {
            val token = "dummyJwtToken"
            val email = "user@example.com"
            val user = createUser().copy(id = 1L, email = email)
            val item = ChangePasswordSecondStepDto(newPassword = "newPass", verificationCode = "code123")

            every { jwtUtil.getUserFromToken(token) } returns user
            every {
                emailVerificationCodeVerifier.verify(
                    email = email,
                    verificationCode = any(),
                    token = any(),
                    mailEventTypes = MailEventTypes.PASSWORD_RESET,
                )
            } throws BadRequestException("The maximum number of attempts has been reached. A new code has been sent to the mail")
            every { passwordEncoder.encode(any()) } returns "encodedNewPass"
            every { repository.save(any()) } returns user
            every { verificationCodesCacheClient.deleteItemFromCache(any()) } just Runs

            val exception = assertThrows<BadRequestException> {
                serviceUnderTest.changePasswordSecondStep(item, token)
            }

            assertThat(exception.message).isEqualTo("The maximum number of attempts has been reached. A new code has been sent to the mail")

            verify { jwtUtil.getUserFromToken(token) }
            verify {
                emailVerificationCodeVerifier.verify(
                    email = email,
                    verificationCode = any(),
                    token = any(),
                    mailEventTypes = MailEventTypes.PASSWORD_RESET,
                )
            }
            verify(exactly = 0) { passwordEncoder.encode(any()) }
            verify { repository wasNot Called }
            verify { verificationCodesCacheClient wasNot Called }
        }
    }
}