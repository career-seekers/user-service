package org.careerseekers.userservice.services.usersService

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.UsersCreator.createUser
import org.careerseekers.userservice.dto.users.UpdateUserDto
import org.careerseekers.userservice.dto.users.VerifyUserDto
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mocks.UsersServiceMocks
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class UsersServiceUpdateTests : UsersServiceMocks() {

    @Nested
    inner class UpdateTests {
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

    @Nested
    inner class VerifyTests {

        @Test
        fun `verifyUser should update users verification`() {
            val user = createUser().copy(verified = false)
            val dto = VerifyUserDto(
                userId = user.id,
                status = true
            )

            every { usersServiceMock.getById(user.id, any(), any()) } returns user

            val result = serviceUnderTest.verifyUser(dto)

            verify(exactly = 1) { usersServiceMock.getById(user.id, any(), any()) }

            assertThat(result).isEqualTo("User verification updated successfully.")
            assertThat(user.verified).isTrue
        }

        @Test
        fun `verifyUser should throw NotFoundException when user not found`() {
            val user = createUser().copy(verified = false)
            val dto = VerifyUserDto(
                userId = user.id,
                status = true
            )

            every { usersServiceMock.getById(user.id, any(), any()) } throws NotFoundException("User with id ${user.id} does not exist.")

            val exception = assertThrows<NotFoundException> {
                serviceUnderTest.verifyUser(dto)
            }

            verify(exactly = 1) { usersServiceMock.getById(user.id, any(), any()) }

            assertThat(exception.message).isEqualTo("User with id ${user.id} does not exist.")
        }
    }
}