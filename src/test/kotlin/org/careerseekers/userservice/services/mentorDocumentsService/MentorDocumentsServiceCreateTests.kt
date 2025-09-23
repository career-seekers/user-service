package org.careerseekers.userservice.services.mentorDocumentsService

import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.exceptions.BadRequestException
import org.careerseekers.userservice.exceptions.DoubleRecordException
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mocks.MentorDocumentsServiceMocks
import org.careerseekers.userservice.mocks.generators.DocumentsGenerator.createMentorDocuments
import org.careerseekers.userservice.mocks.generators.DocumentsGenerator.createMentorDocumentsDto
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUser
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertFailsWith

class MentorDocumentsServiceCreateTests : MentorDocumentsServiceMocks() {

    @Nested
    inner class CreateTests {

        @Test
        fun `Should create mentor documents and return MentorDocuments`() {
            val user = createUser().copy(role = UsersRoles.MENTOR)
            val dto = createMentorDocumentsDto(user)
            val documents = createMentorDocuments(user)

            every { usersService.getById(user.id, any(), any()) } returns user
            every { serviceUnderTest.getDocsByUserId(user.id, throwable = false) } returns null
            every { serviceUnderTest["createMentorDocument"](dto, user) } returns documents
            every { repository.save(any()) } returns documents

            val result = serviceUnderTest.create(dto)

            assertThat(result).isEqualTo(documents)

            verify { usersService.getById(user.id, any(), any()) }
            verify { serviceUnderTest.getDocsByUserId(user.id, throwable = false) }
            verify { repository.save(any()) }
        }

        @Test
        fun `Should return NotFoundException when user not found`() {
            val user = createUser().copy(role = UsersRoles.MENTOR)
            val dto = createMentorDocumentsDto(user)

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
        fun `Should throw BadRequestException when user exist but it's role not MENTOR`() {
            val user = createUser().copy(role = UsersRoles.ADMIN)
            val dto = createMentorDocumentsDto(user)

            every { usersService.getById(user.id, any(), any()) } returns user

            val exception = assertFailsWith<BadRequestException> {
                serviceUnderTest.create(dto)
            }

            assertThat(exception.message).isEqualTo("У этого пользователя есть роль ${user.role}, а не ${UsersRoles.MENTOR}. Пожалуйста, используйте другой контроллер для создания его документов.")

            verify { usersService.getById(user.id, any(), any()) }

            verify(exactly = 0) { serviceUnderTest.getDocsByUserId(user.id, throwable = false) }
            verify(exactly = 0) { repository.save(any()) }
        }

        @Test
        fun `Should throw DoubleRecordException when user exist and already has documents`() {
            val user = createUser().copy(role = UsersRoles.MENTOR)
            val dto = createMentorDocumentsDto(user)
            val documents = createMentorDocuments(user)

            every { usersService.getById(user.id, any(), any()) } returns user
            every { serviceUnderTest.getDocsByUserId(user.id, throwable = false) } returns documents

            val exception = assertFailsWith<DoubleRecordException> {
                serviceUnderTest.create(dto)
            }

            assertThat(exception.message).isEqualTo("У этого пользователя уже есть документы. Если вы хотите изменить его, используйте метод обновления.")

            verify { usersService.getById(user.id, any(), any()) }
            verify { serviceUnderTest.getDocsByUserId(user.id, throwable = false) }

            verify(exactly = 0) { repository.save(any()) }
        }
    }
}