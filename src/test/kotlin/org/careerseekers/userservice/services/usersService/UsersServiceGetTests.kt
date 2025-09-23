package org.careerseekers.userservice.services.usersService

import io.mockk.Called
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUser
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mocks.UsersServiceMocks
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExtendWith(MockKExtension::class)
class UsersServiceGetTests : UsersServiceMocks() {

    @Nested
    inner class GetAll {

        @Test
        fun `getAll should return all users from repository`() {
            val user1 = createUser()
            val user2 = createUser()
            val userList = listOf(user1, user2)

            every { repository.findAll() } returns userList

            val result = serviceUnderTest.getAll()

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

            val result = serviceUnderTest.getById(userId, throwable = true, message = "Пользователь с ID $userId не найден.")

            assertEquals(user, result)
            verify { repository.findById(userId) }
        }

        @Test
        fun `getById should throw NotFoundException when user does not exist and throwable true`() {
            val userId = 2L

            every { repository.findById(userId) } returns Optional.empty()

            val exception = assertFailsWith<NotFoundException> {
                serviceUnderTest.getById(userId, throwable = true, message = "Пользователь с ID $userId не найден.")
            }

            assertEquals("Пользователь с ID $userId не найден.", exception.message)
            verify { repository.findById(userId) }
        }

        @Test
        fun `getById should return null when user does not exist and throwable false`() {
            val userId = 3L

            every { repository.findById(userId) } returns Optional.empty()

            val result = serviceUnderTest.getById(userId, throwable = false, message = "Пользователь с ID $userId не найден.")

            assertNull(result)
            verify { repository.findById(userId) }
        }

        @Test
        fun `getById should throw NotFoundException when id is null and throwable true`() {
            val exception = assertFailsWith<NotFoundException> {
                serviceUnderTest.getById(null, throwable = true, message = "User with id null not found")
            }
            assertEquals("ID cannot be null.", exception.message)
            verify { repository wasNot Called }
        }

        @Test
        fun `getById should return null when id is null and throwable false`() {
            val result = serviceUnderTest.getById(null, throwable = false, message = "User with id null not found")

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

            val result = serviceUnderTest.getByEmail(email, throwable = true)

            assertEquals(user, result)
            verify { repository.getByEmail(email) }
        }

        @Test
        fun `getByEmail should throw NotFoundException when user does not exist and throwable true`() {
            val email = "notfound@example.com"

            every { repository.getByEmail(email) } returns null

            val exception = assertFailsWith<NotFoundException> {
                serviceUnderTest.getByEmail(email, throwable = true)
            }

            assertEquals("Пользователь с адресом электронной почты $email не найден.", exception.message)
            verify { repository.getByEmail(email) }
        }

        @Test
        fun `getByEmail should return null when user does not exist and throwable false`() {
            val email = "notfound2@example.com"

            every { repository.getByEmail(email) } returns null

            val result = serviceUnderTest.getByEmail(email, throwable = false)

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

            val result = serviceUnderTest.getByMobileNumber(mobileNumber, throwable = true)

            assertEquals(user, result)
            verify { repository.getByMobileNumber(mobileNumber) }
        }

        @Test
        fun `getByMobileNumber should throw NotFoundException when user does not exist and throwable true`() {
            val mobileNumber = "+70987654321"

            every { repository.getByMobileNumber(mobileNumber) } returns null

            val exception = assertFailsWith<NotFoundException> {
                serviceUnderTest.getByMobileNumber(mobileNumber, throwable = true)
            }

            assertEquals("Пользователь с номером мобильного телефона $mobileNumber не найден.", exception.message)
            verify { repository.getByMobileNumber(mobileNumber) }
        }

        @Test
        fun `getByMobileNumber should return null when user does not exist and throwable false`() {
            val mobileNumber = "+70000000000"

            every { repository.getByMobileNumber(mobileNumber) } returns null

            val result = serviceUnderTest.getByMobileNumber(mobileNumber, throwable = false)

            assertNull(result)
            verify { repository.getByMobileNumber(mobileNumber) }
        }
    }

    @Nested
    inner class GetByRole {
        @Test
        fun `Should return list of users with role ADMIN`() {
            val users = MutableList(10) { createUser().copy(role = UsersRoles.USER) }

            users[0].role = UsersRoles.ADMIN
            users[5].role = UsersRoles.ADMIN

            every { repository.getByRole(UsersRoles.ADMIN) } returns listOf(users[0], users[5])

            val resultAdmin = serviceUnderTest.getByRole(UsersRoles.ADMIN)

            assertThat(resultAdmin.size == 2)
            assertThat(resultAdmin.forEach { user -> user.role = UsersRoles.ADMIN })
        }
    }
}