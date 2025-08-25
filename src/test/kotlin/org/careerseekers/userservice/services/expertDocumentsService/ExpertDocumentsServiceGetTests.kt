package org.careerseekers.userservice.services.expertDocumentsService

import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.entities.ExpertDocuments
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.exceptions.BadRequestException
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mocks.ExpertDocumentsServiceMocks
import org.careerseekers.userservice.mocks.generators.DocumentsGenerator.createExpertDocuments
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUser
import org.junit.jupiter.api.Nested
import java.util.Optional
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ExpertDocumentsServiceGetTests : ExpertDocumentsServiceMocks() {

    @Nested
    inner class GetAllTests {

        @Test
        fun `Should return a list of expert documents`() {
            val documents = List(5) { createExpertDocuments(createUser().copy(role = UsersRoles.EXPERT)) }

            every { repository.findAll() } returns documents

            val result = serviceUnderTest.getAll()

            assertThat(result).isEqualTo(documents)
            verify { repository.findAll() }
        }

        @Test
        fun `Should return empty list of mentor documents`() {
            val documents = emptyList<ExpertDocuments>()

            every { repository.findAll() } returns documents

            val result = serviceUnderTest.getAll()

            assertThat(result).isEqualTo(documents)
            verify { repository.findAll() }
        }
    }

    @Nested
    inner class GetByIdTests {

        @Test
        fun `Should return expert document by id`() {
            val documents = createExpertDocuments(createUser().copy(role = UsersRoles.EXPERT))

            every { repository.findById(documents.id) } returns Optional.of(documents)

            val result = serviceUnderTest.getById(documents.id)

            assertThat(result).isNotNull.isEqualTo(documents)
            verify { repository.findById(documents.id) }
        }

        @Test
        fun `Should return NotFoundException when documents not found and throwable = true`() {
            val documents = createExpertDocuments(createUser().copy(role = UsersRoles.EXPERT))
            every { repository.findById(any()) } returns Optional.empty()

            val exception = assertFailsWith<NotFoundException> {
                serviceUnderTest.getById(documents.id, message = "Expert documents with id ${documents.id} not found")
            }

            assertThat(exception.message).isEqualTo("Expert documents with id ${documents.id} not found")
            verify { repository.findById(any()) }
        }

        @Test
        fun `Should return null when documents not found and throwable = false`() {
            val documents = createExpertDocuments(createUser().copy(role = UsersRoles.EXPERT))
            every { repository.findById(any()) } returns Optional.empty()

            val result = serviceUnderTest.getById(documents.id, false)

            assertThat(result).isNull()
            verify { repository.findById(any()) }
        }
    }

    @Nested
    inner class GetDocsByUserIdTests {

        @Test
        fun `Should return mentor documents by user id`() {
            val user = createUser().copy(role = UsersRoles.EXPERT)
            val documents = createExpertDocuments(user)

            every { usersService.getById(user.id, any(), any()) } returns user
            every { repository.findByUserId(user.id) } returns documents

            val result = serviceUnderTest.getDocsByUserId(user.id)

            assertThat(result).isEqualTo(documents)

            verify { usersService.getById(user.id, any(), any()) }
            verify { repository.findByUserId(user.id) }
        }

        @Test
        fun `Should throw NotFoundException when documents not found and throwable = true`() {
            val user = createUser().copy(role = UsersRoles.EXPERT)

            every { usersService.getById(user.id, any(), any()) } returns user
            every { repository.findByUserId(user.id) } returns null

            val exception = assertFailsWith<NotFoundException> {
                serviceUnderTest.getDocsByUserId(user.id)
            }

            assertThat(exception.message).isEqualTo("Documents for user with id ${user.id} not found")

            verify { usersService.getById(user.id, any(), any()) }
            verify { repository.findByUserId(user.id) }
        }

        @Test
        fun `Should return null when documents not found and throwable = false`() {
            val user = createUser().copy(role = UsersRoles.EXPERT)

            every { usersService.getById(user.id, any(), any()) } returns user
            every { repository.findByUserId(user.id) } returns null

            val result = serviceUnderTest.getDocsByUserId(user.id, throwable = false)

            assertThat(result).isNull()

            verify { usersService.getById(user.id, any(), any()) }
            verify { repository.findByUserId(user.id) }
        }

        @Test
        fun `Should return NotFoundException when user not found`() {
            val user = createUser().copy(role = UsersRoles.EXPERT)

            every {
                usersService.getById(
                    user.id,
                    any(),
                    any()
                )
            } throws NotFoundException("User with id ${user.id} not found")

            val exception = assertFailsWith<NotFoundException> {
                serviceUnderTest.getDocsByUserId(user.id)
            }

            assertThat(exception.message).isEqualTo("User with id ${user.id} not found")

            verify { usersService.getById(user.id, any(), any()) }
            verify(exactly = 0) { repository.findByUserId(user.id) }
        }

        @Test
        fun `Should throw BadRequestException if user role is not expert`() {
            val user = createUser().copy(role = UsersRoles.ADMIN)

            every { usersService.getById(user.id, any(), any()) } returns user

            val exception = assertFailsWith<BadRequestException> {
                serviceUnderTest.getDocsByUserId(user.id)
            }

            assertThat(exception.message).isEqualTo("This user has role ${user.role}, not ${UsersRoles.EXPERT}. Please use another controller to check his documents.")

            verify { usersService.getById(user.id, any(), any()) }
            verify(exactly = 0) { repository.findByUserId(user.id) }
        }
    }
}