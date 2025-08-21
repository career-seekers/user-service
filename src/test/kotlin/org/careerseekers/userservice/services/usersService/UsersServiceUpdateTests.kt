package org.careerseekers.userservice.services.usersService

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.UsersCreator.createUser
import org.careerseekers.userservice.cache.VerificationCodesCacheClient
import org.careerseekers.userservice.dto.users.UpdateUserDto
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mappers.UsersMapper
import org.careerseekers.userservice.repositories.UsersRepository
import org.careerseekers.userservice.services.UsersService
import org.careerseekers.userservice.services.kafka.producers.KafkaEmailSendingProducer
import org.careerseekers.userservice.utils.DocumentExistenceChecker
import org.careerseekers.userservice.utils.EmailVerificationCodeVerifier
import org.careerseekers.userservice.utils.JwtUtil
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockKExtension::class)
class UsersServiceUpdateTests {

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

    @Test
    fun `update should update only provided fields`() {
        val user = createUser().copy(id = 1L, firstName = "FirstName", lastName = "LastName", patronymic = "Patronymic")

        val updateDto = UpdateUserDto(
            id = 1L,
            firstName = "NewFirstName",
            lastName = null,
            patronymic = "NewPatronymic"
        )

        every { usersServiceMock.getById(1L, any(), any()) } returns user


        val result = serviceUnderTest.update(updateDto)
        assertThat(result).isEqualTo("User updated successfully.")

        assertThat(user.firstName).isEqualTo("NewFirstName")
        assertThat(user.lastName).isEqualTo("LastName")
        assertThat(user.patronymic).isEqualTo("NewPatronymic")

        verify { usersServiceMock.getById(1L, any(), any()) }
    }

    @Test
    fun `update should throw when user not found`() {
        val dto = UpdateUserDto(id = 1L)

        every { usersServiceMock.getById(1L, any(), any()) } throws NotFoundException("User with id ${dto.id} not found")

        val exception = assertThrows<NotFoundException> {
            serviceUnderTest.update(dto)
        }

        assertThat(exception.message).isEqualTo("User with id ${dto.id} not found")
        verify { usersServiceMock.getById(1L, any(), any()) }
    }
}