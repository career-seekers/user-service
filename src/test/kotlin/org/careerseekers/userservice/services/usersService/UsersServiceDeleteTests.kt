package org.careerseekers.userservice.services.usersService

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.UsersCreator.createUser
import org.careerseekers.userservice.cache.VerificationCodesCacheClient
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mappers.UsersMapper
import org.careerseekers.userservice.repositories.UsersRepository
import org.careerseekers.userservice.services.UsersService
import org.careerseekers.userservice.services.kafka.producers.KafkaEmailSendingProducer
import org.careerseekers.userservice.utils.DocumentExistenceChecker
import org.careerseekers.userservice.utils.EmailVerificationCodeVerifier
import org.careerseekers.userservice.utils.JwtUtil
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class UsersServiceDeleteTests {

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
    inner class DeleteTests {

        @Test
        fun `deleteById should delete user`() {
            val user = createUser()

            every { usersServiceMock.getById(user.id, any(), any()) } returns user
            every { repository.delete(user) } returns Unit

            val result = serviceUnderTest.deleteById(user.id)

            assertThat(result).isEqualTo("User deleted successfully.")

            verify(exactly = 1) { usersServiceMock.getById(user.id, any(), any()) }
            verify(exactly = 1) { repository.delete(user) }
        }

        @Test
        fun `deleteById should return NotFoundException if user does not exists`() {
            val user = createUser()

            every { usersServiceMock.getById(user.id, any(), any()) } throws NotFoundException("User with id ${user.id} does not exist.")

            val exception = assertThrows<NotFoundException> {
                serviceUnderTest.deleteById(user.id)
            }

            assertThat(exception.message).isEqualTo("User with id ${user.id} does not exist.")

            verify(exactly = 1) { usersServiceMock.getById(user.id, any(), any()) }
        }
    }
}