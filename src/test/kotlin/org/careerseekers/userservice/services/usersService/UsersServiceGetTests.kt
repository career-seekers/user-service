package org.careerseekers.userservice.services.usersService

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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Optional
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class UsersServiceGetTests {
    @Mock lateinit var repository: UsersRepository
    @Mock lateinit var jwtUtil: JwtUtil
    @Mock lateinit var usersMapper: UsersMapper
    @Mock lateinit var passwordEncoder: PasswordEncoder
    @Mock lateinit var emailSendingProducer: KafkaEmailSendingProducer
    @Mock lateinit var verificationCodesCacheClient: VerificationCodesCacheClient
    @Mock lateinit var documentExistenceChecker: DocumentExistenceChecker
    @Mock lateinit var emailVerificationCodeVerifier: EmailVerificationCodeVerifier

    @InjectMocks lateinit var usersService: UsersService

    @Nested
    inner class GetAll {

        @Test
        fun `getAll should return all users from repository`() {
            val user1 = createUser()
            val user2 = createUser()
            val userList = listOf(user1, user2)

            `when`(repository.findAll()).thenReturn(userList)

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

            `when`(repository.findById(userId)).thenReturn(Optional.of(user))

            val result = usersService.getById(userId, throwable = true, message = "User with id $userId not found")

            assertEquals(user, result)
            verify(repository).findById(userId)
        }

        @Test
        fun `getById should throw NotFoundException when user does not exist and throwable true`() {
            val userId = 2L

            `when`(repository.findById(userId)).thenReturn(Optional.empty())

            val exception = assertThrows<NotFoundException> {
                usersService.getById(userId, throwable = true, message = "User with id $userId not found")
            }

            assertEquals("User with id $userId not found", exception.message)
            verify(repository).findById(userId)
        }

        @Test
        fun `getById should return null when user does not exist and throwable false`() {
            val userId = 3L

            `when`(repository.findById(userId)).thenReturn(Optional.empty())

            val result = usersService.getById(userId, throwable = false, message = "User with id $userId not found")

            assertNull(result)
            verify(repository).findById(userId)
        }

        @Test
        fun `getById should throw NotFoundException when id is null and throwable true`() {
            val exception = assertThrows<NotFoundException> {
                usersService.getById(null, throwable = true, message = "User with id null not found")
            }
            assertEquals("ID cannot be null.", exception.message)
            verify(repository, never()).findById(any())
        }

        @Test
        fun `getById should return null when id is null and throwable false`() {
            val result = usersService.getById(null, throwable = false, message = "User with id null not found")

            assertNull(result)
            verify(repository, never()).findById(any())
        }
    }

    @Nested
    inner class GetByEmail {

        @Test
        fun `getByEmail should return user when user exists`() {
            val email = "test@example.com"
            val user = createUser().copy(email = email)

            `when`(repository.getByEmail(email)).thenReturn(user)

            val result = usersService.getByEmail(email, throwable = true)

            assertEquals(user, result)
            verify(repository).getByEmail(email)
        }

        @Test
        fun `getByEmail should throw NotFoundException when user does not exist and throwable true`() {
            val email = "notfound@example.com"

            `when`(repository.getByEmail(email)).thenReturn(null)

            val exception = assertThrows<NotFoundException> {
                usersService.getByEmail(email, throwable = true)
            }

            assertEquals("User with email $email not found", exception.message)
            verify(repository).getByEmail(email)
        }

        @Test
        fun `getByEmail should return null when user does not exist and throwable false`() {
            val email = "notfound2@example.com"

            `when`(repository.getByEmail(email)).thenReturn(null)

            val result = usersService.getByEmail(email, throwable = false)

            assertNull(result)
            verify(repository).getByEmail(email)
        }
    }

    @Nested
    inner class GetByMobileNumber {

        @Test
        fun `getByMobileNumber should return user when user exists`() {
            val mobileNumber = "+71234567890"
            val user = createUser().copy(mobileNumber = mobileNumber)

            `when`(repository.getByMobileNumber(mobileNumber)).thenReturn(user)

            val result = usersService.getByMobileNumber(mobileNumber, throwable = true)

            assertEquals(user, result)
            verify(repository).getByMobileNumber(mobileNumber)
        }

        @Test
        fun `getByMobileNumber should throw NotFoundException when user does not exist and throwable true`() {
            val mobileNumber = "+70987654321"

            `when`(repository.getByMobileNumber(mobileNumber)).thenReturn(null)

            val exception = assertThrows<NotFoundException> {
                usersService.getByMobileNumber(mobileNumber, throwable = true)
            }

            assertEquals("User with mobile number $mobileNumber not found", exception.message)
            verify(repository).getByMobileNumber(mobileNumber)
        }

        @Test
        fun `getByMobileNumber should return null when user does not exist and throwable false`() {
            val mobileNumber = "+70000000000"

            `when`(repository.getByMobileNumber(mobileNumber)).thenReturn(null)

            val result = usersService.getByMobileNumber(mobileNumber, throwable = false)

            assertNull(result)
            verify(repository).getByMobileNumber(mobileNumber)
        }
    }
}