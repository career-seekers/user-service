package org.careerseekers.userservice.services.expertDocumentsService

import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.exceptions.BadRequestException
import org.careerseekers.userservice.exceptions.DoubleRecordException
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mocks.ExpertDocumentsServiceMocks
import org.careerseekers.userservice.mocks.generators.DocumentsGenerator.createExpertDocuments
import org.careerseekers.userservice.mocks.generators.DocumentsGenerator.createExpertDocumentsDto
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUser
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ExpertDocumentsServiceCreateTests : ExpertDocumentsServiceMocks() {

    @Nested
    inner class CreateTests {

        @Test
        fun `Should create expert documents and return ExpertDocuments`() {
            val user = createUser().copy(role = UsersRoles.EXPERT)
            val dto = createExpertDocumentsDto(user)
            val documents = createExpertDocuments(user)

            every { usersService.getById(user.id, any(), any()) } returns user
            every { serviceUnderTest.getDocsByUserId(user.id, throwable = false) } returns null
            every { expertDocumentsMapper.expertDocsFromDto(any()) } returns documents
            every { repository.save(any()) } returns documents

            val result = serviceUnderTest.create(dto)

            assertThat(result).isEqualTo(documents)

            verify { usersService.getById(user.id, any(), any()) }
            verify { serviceUnderTest.getDocsByUserId(user.id, throwable = false) }
            verify { repository.save(any()) }
        }

        @Test
        fun `Should return NotFoundException when user not found`() {
            val user = createUser().copy(role = UsersRoles.EXPERT)
            val dto = createExpertDocumentsDto(user)

            every {
                usersService.getById(
                    user.id,
                    any(),
                    any()
                )
            } throws NotFoundException("User with id ${user.id} not found.")

            val exception = assertFailsWith<NotFoundException> {
                serviceUnderTest.create(dto)
            }

            assertThat(exception.message).isEqualTo("User with id ${user.id} not found.")

            verify { usersService.getById(user.id, any(), any()) }

            verify(exactly = 0) { serviceUnderTest.getDocsByUserId(user.id, throwable = false) }
            verify(exactly = 0) { repository.save(any()) }
        }

        @Test
        fun `Should throw BadRequestException when user exist but it's role not EXPERT`() {
            val user = createUser().copy(role = UsersRoles.ADMIN)
            val dto = createExpertDocumentsDto(user)

            every { usersService.getById(user.id, any(), any()) } returns user

            val exception = assertFailsWith<BadRequestException> {
                serviceUnderTest.create(dto)
            }

            assertThat(exception.message).isEqualTo("This user has role ${user.role}, not ${UsersRoles.EXPERT}. Please use another controller to create his documents.")

            verify { usersService.getById(user.id, any(), any()) }

            verify(exactly = 0) { serviceUnderTest.getDocsByUserId(user.id, throwable = false) }
            verify(exactly = 0) { repository.save(any()) }
        }

        @Test
        fun `Should throw DoubleRecordException when user exist and already has documents`() {
            val user = createUser().copy(role = UsersRoles.EXPERT)
            val dto = createExpertDocumentsDto(user)
            val documents = createExpertDocuments(user)

            every { usersService.getById(user.id, any(), any()) } returns user
            every { serviceUnderTest.getDocsByUserId(user.id, throwable = false) } returns documents

            val exception = assertFailsWith<DoubleRecordException> {
                serviceUnderTest.create(dto)
            }

            assertThat(exception.message).isEqualTo("This user already has documents. If you want to change it, use update method.")

            verify { usersService.getById(user.id, any(), any()) }
            verify { serviceUnderTest.getDocsByUserId(user.id, throwable = false) }

            verify(exactly = 0) { repository.save(any()) }
        }
    }
}