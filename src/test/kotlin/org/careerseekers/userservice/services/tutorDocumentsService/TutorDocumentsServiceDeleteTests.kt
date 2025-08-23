package org.careerseekers.userservice.services.tutorDocumentsService

import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.careerseekers.userservice.mocks.TutorDocumentsServiceMocks
import org.careerseekers.userservice.mocks.generators.DocumentsGenerator.createTutorDocuments
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUser
import org.junit.jupiter.api.Nested
import org.mockito.ArgumentMatchers.any
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
            every { documentsApiResolver.deleteDocument(any(), any()) } returns BasicSuccessfulResponse(any<String>())

            val result = serviceUnderTest.deleteById(documents.id)

            assertThat(result).isEqualTo("Tutor documents deleted successfully.")

            assertThat(user.tutorDocuments).isNull()

            verify { serviceUnderTest.getById(documents.id, any(), any()) }
            verify { usersRepository.save(any()) }
            verify { repository.delete(documents) }
            verify { documentsApiResolver.deleteDocument(any(), any()) }
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
            } throws NotFoundException("Tutor documents not found.")

            val exception = assertFailsWith<NotFoundException> { serviceUnderTest.deleteById(documents.id) }

            assertThat(exception.message).isEqualTo("Tutor documents not found.")

            verify { serviceUnderTest.getById(documents.id, any(), any()) }

            verify(exactly = 0) { usersRepository.save(any()) }
            verify(exactly = 0) { repository.delete(documents) }
            verify(exactly = 0) { documentsApiResolver.deleteDocument(any(), any()) }
        }
    }
}