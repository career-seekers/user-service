package org.careerseekers.userservice.services.authService

import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUser
import org.careerseekers.userservice.dto.auth.PreRegisterUserDto
import org.careerseekers.userservice.exceptions.DoubleRecordException
import org.careerseekers.userservice.mocks.AuthServiceMocks
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class AuthServicePreRegisterTests : AuthServiceMocks() {

    @Nested
    inner class PreRegisterTests {
        private val user = createUser()
        private val inputDto = PreRegisterUserDto(
            email = user.email,
            mobileNumber = user.mobileNumber,
        )

        @Test
        fun `preRegister should return BasicSuccessfulResponse when user is not registered yet`() {
            every { usersService.getByEmail(user.email, eq(false)) } returns null
            every { usersService.getByMobileNumber(user.mobileNumber, eq(false)) } returns null
            every { emailSendingProducer.sendMessage(any()) } returns Unit

            val result = serviceUnderTest.preRegister(inputDto)

            assertThat(result).isNotNull

            verify { usersService.getByEmail(user.email, false) }
            verify { usersService.getByMobileNumber(user.mobileNumber, false) }
            verify { emailSendingProducer.sendMessage(any()) }
        }

        @Test
        fun `preRegister should return DoubleRecordException when user with similar email exist`() {
            every { usersService.getByEmail(user.email, eq(false)) } returns user

            val exception = assertFailsWith<DoubleRecordException> { serviceUnderTest.preRegister(inputDto) }

            assertThat(exception.message).isEqualTo("User with email ${user.email} already exists")

            verify { usersService.getByEmail(user.email, false) }
            verify(exactly = 0) { usersService.getByMobileNumber(any(), any()) }
            verify(exactly = 0) { emailSendingProducer.sendMessage(any()) }
        }

        @Test
        fun `preRegister should throw DoubleRecordException when user with similar mobile number exist`() {
            every { usersService.getByEmail(user.email, eq(false)) } returns null
            every { usersService.getByMobileNumber(user.mobileNumber, eq(false)) } returns user

            val exception = assertFailsWith<DoubleRecordException> { serviceUnderTest.preRegister(inputDto) }

            assertThat(exception.message).isEqualTo("User with mobile number ${user.mobileNumber} already exists")

            verify { usersService.getByEmail(user.email, false) }
            verify { usersService.getByMobileNumber(user.mobileNumber, false) }
            verify(exactly = 0) { emailSendingProducer.sendMessage(any()) }
        }
    }
}