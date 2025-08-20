package org.careerseekers.userservice.services.usersService

import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
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
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Optional
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class UsersServiceGetTests {
    @MockK lateinit var repository: UsersRepository
    @MockK lateinit var jwtUtil: JwtUtil
    @MockK lateinit var usersMapper: UsersMapper
    @MockK lateinit var passwordEncoder: PasswordEncoder
    @MockK lateinit var emailSendingProducer: KafkaEmailSendingProducer
    @MockK lateinit var verificationCodesCacheClient: VerificationCodesCacheClient
    @MockK lateinit var documentExistenceChecker: DocumentExistenceChecker
    @MockK lateinit var emailVerificationCodeVerifier: EmailVerificationCodeVerifier

    lateinit var usersService: UsersService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        usersService = UsersService(
            repository,
            jwtUtil,
            usersMapper,
            passwordEncoder,
            emailSendingProducer,
            verificationCodesCacheClient,
            documentExistenceChecker,
            emailVerificationCodeVerifier,
            usersService = null
        )
    }

    @Nested
    inner class GetAll {

        @Test
        fun `getAll should return all users from repository`() {
            val user1 = createUser()
            val user2 = createUser()
            val userList = listOf(user1, user2)

            every { repository.findAll() } returns userList

            val result = usersService.getAll()

            assertEquals(userList, result)
        }
    }

    @Nested
    inner class GetById {

        @Test
        fun `getById should return user when user exists`() {
            val userId = 1L
            val user = createUser().copy(id = userId)

            every { repository.findById(userId) } returns Optional.of(user)

            val result = usersService.getById(userId, throwable = true, message = "User with id $userId not found")

            assertEquals(user, result)
            verify { repository.findById(userId) }
        }

        @Test
        fun `getById should throw NotFoundException when user does not exist and throwable true`() {
            val userId = 2L

            every { repository.findById(userId) } returns Optional.empty()

            val exception = assertThrows<NotFoundException> {
                usersService.getById(userId, throwable = true, message = "User with id $userId not found")
            }

            assertEquals("User with id $userId not found", exception.message)
            verify { repository.findById(userId) }
        }

        @Test
        fun `getById should return null when user does not exist and throwable false`() {
            val userId = 3L

            every { repository.findById(userId) } returns Optional.empty()

            val result = usersService.getById(userId, throwable = false, message = "User with id $userId not found")

            assertNull(result)
            verify { repository.findById(userId) }
        }

        @Test
        fun `getById should throw NotFoundException when id is null and throwable true`() {
            val exception = assertThrows<NotFoundException> {
                usersService.getById(null, throwable = true, message = "User with id null not found")
            }
            assertEquals("ID cannot be null.", exception.message)
            verify { repository wasNot Called }
        }

        @Test
        fun `getById should return null when id is null and throwable false`() {
            val result = usersService.getById(null, throwable = false, message = "User with id null not found")

            assertNull(result)
            verify { repository wasNot Called }
        }
    }

    @Nested
    inner class GetByEmail {

        @Test
        fun `getByEmail should return user when user exists`() {
            val email = "test@example.com"
            val user = createUser().copy(email = email)

            every { repository.getByEmail(email) } returns user

            val result = usersService.getByEmail(email, throwable = true)

            assertEquals(user, result)
            verify { repository.getByEmail(email) }
        }

        @Test
        fun `getByEmail should throw NotFoundException when user does not exist and throwable true`() {
            val email = "notfound@example.com"

            every { repository.getByEmail(email) } returns null

            val exception = assertThrows<NotFoundException> {
                usersService.getByEmail(email, throwable = true)
            }

            assertEquals("User with email $email not found", exception.message)
            verify { repository.getByEmail(email) }
        }

        @Test
        fun `getByEmail should return null when user does not exist and throwable false`() {
            val email = "notfound2@example.com"

            every { repository.getByEmail(email) } returns null

            val result = usersService.getByEmail(email, throwable = false)

            assertNull(result)
            verify { repository.getByEmail(email) }
        }
    }

    @Nested
    inner class GetByMobileNumber {

        @Test
        fun `getByMobileNumber should return user when user exists`() {
            val mobileNumber = "+71234567890"
            val user = createUser().copy(mobileNumber = mobileNumber)

            every { repository.getByMobileNumber(mobileNumber) } returns user

            val result = usersService.getByMobileNumber(mobileNumber, throwable = true)

            assertEquals(user, result)
            verify { repository.getByMobileNumber(mobileNumber) }
        }

        @Test
        fun `getByMobileNumber should throw NotFoundException when user does not exist and throwable true`() {
            val mobileNumber = "+70987654321"

            every { repository.getByMobileNumber(mobileNumber) } returns null

            val exception = assertThrows<NotFoundException> {
                usersService.getByMobileNumber(mobileNumber, throwable = true)
            }

            assertEquals("User with mobile number $mobileNumber not found", exception.message)
            verify { repository.getByMobileNumber(mobileNumber) }
        }

        @Test
        fun `getByMobileNumber should return null when user does not exist and throwable false`() {
            val mobileNumber = "+70000000000"

            every { repository.getByMobileNumber(mobileNumber) } returns null

            val result = usersService.getByMobileNumber(mobileNumber, throwable = false)

            assertNull(result)
            verify { repository.getByMobileNumber(mobileNumber) }
        }
    }
}