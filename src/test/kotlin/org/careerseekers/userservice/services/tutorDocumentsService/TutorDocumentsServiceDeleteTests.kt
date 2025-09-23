package org.careerseekers.userservice.services.tutorDocumentsService

import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mocks.TutorDocumentsServiceMocks
import org.careerseekers.userservice.mocks.generators.DocumentsGenerator.createTutorDocuments
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUser
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertFailsWith

class TutorDocumentsServiceDeleteTests : TutorDocumentsServiceMocks() {

    @Nested
    inner class DeleteByIdTests {

        @Test
        fun `Should delete documents and return String`() {
            val user = createUser()
            val documents = createTutorDocuments(user)

            every { serviceUnderTest.getById(documents.id, any(), any()) } returns documents
            every { usersRepository.save(any()) } returns user
            every { repository.delete(any()) } returns Unit

            val result = serviceUnderTest.deleteById(documents.id)

            assertThat(result).isEqualTo("Документы куратора успешно удалены.")

            assertThat(user.tutorDocuments).isNull()

            verify { serviceUnderTest.getById(documents.id, any(), any()) }
            verify { usersRepository.save(any()) }
            verify { repository.delete(documents) }
        }

        @Test
        fun `Should return NotFoundException if documents not found`() {
            val user = createUser()
            val documents = createTutorDocuments(user)

            every {
                serviceUnderTest.getById(
                    documents.id,
                    any(),
                    any()
                )
            } throws NotFoundException("Документы куратора не найдены.")

            val exception = assertFailsWith<NotFoundException> { serviceUnderTest.deleteById(documents.id) }

            assertThat(exception.message).isEqualTo("Документы куратора не найдены.")

            verify { serviceUnderTest.getById(documents.id, any(), any()) }

            verify(exactly = 0) { usersRepository.save(any()) }
            verify(exactly = 0) { repository.delete(documents) }
        }
    }

    @Nested
    inner class DeleteAllTests {
        @Test
        fun `Should delete all documents and return String`() {
            val documents = List(5) { createTutorDocuments(createUser().copy(role = UsersRoles.TUTOR)) }

            every { repository.findAll() } returns documents
            every { serviceUnderTest.deleteById(any()) } returns "Документы куратора успешно удалены."

            val result = serviceUnderTest.deleteAll()

            assertThat(result).isEqualTo("Все документы куратора успешно удалены.")

            verify { repository.findAll() }
            verify(exactly = 5) { serviceUnderTest.deleteById(any()) }
        }
    }
}