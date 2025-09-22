package org.careerseekers.userservice.services.usersService

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUser
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mocks.UsersServiceMocks
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.Test
import kotlin.test.assertFailsWith

@ExtendWith(MockKExtension::class)
class UsersServiceDeleteTests : UsersServiceMocks() {
    @Nested
    inner class DeleteTests {

        @Test
        fun `deleteById should delete user`() {
            val user = createUser()

            every { usersServiceMock.getById(user.id, any(), any()) } returns user
            every { repository.delete(user) } returns Unit

            val result = serviceUnderTest.deleteById(user.id)

            assertThat(result).isEqualTo("Пользователь удалён успешно.")

            verify(exactly = 1) { usersServiceMock.getById(user.id, any(), any()) }
            verify(exactly = 1) { repository.delete(user) }
        }

        @Test
        fun `deleteById should return NotFoundException if user does not exist`() {
            val user = createUser()

            every { usersServiceMock.getById(user.id, any(), any()) } throws NotFoundException("User with id ${user.id} does not exist.")

            val exception = assertFailsWith<NotFoundException> {
                serviceUnderTest.deleteById(user.id)
            }

            assertThat(exception.message).isEqualTo("User with id ${user.id} does not exist.")

            verify(exactly = 1) { usersServiceMock.getById(user.id, any(), any()) }
        }
    }

    @Nested
    inner class DeleteAllTests {

        @Test
        fun `deleteAll should delete all users`() {
            every { repository.deleteAll() } returns Unit

            val result = serviceUnderTest.deleteAll()

            assertThat(result).isEqualTo("Все пользователи удалены успешно.")

            verify(exactly = 1) { repository.deleteAll() }
        }
    }
}