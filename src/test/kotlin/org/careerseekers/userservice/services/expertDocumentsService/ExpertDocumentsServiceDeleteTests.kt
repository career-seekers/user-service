package org.careerseekers.userservice.services.expertDocumentsService

import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.careerseekers.userservice.mocks.ExpertDocumentsServiceMocks
import org.careerseekers.userservice.mocks.generators.DocumentsGenerator.createExpertDocuments
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUser
import org.junit.jupiter.api.Nested
import org.mockito.ArgumentMatchers.any
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ExpertDocumentsServiceDeleteTests : ExpertDocumentsServiceMocks() {

    @Nested
    inner class DeleteByIdTests {

        @Test
        fun `Should delete documents and return String`() {
            val user = createUser().copy(role = UsersRoles.EXPERT)
            val documents = createExpertDocuments(user)

            every { serviceUnderTest.getById(documents.id, any(), any()) } returns documents
            every { usersRepository.save(any()) } returns user
            every { repository.delete(any()) } returns Unit
            every { documentsApiResolver.deleteDocument(any(), any()) } returns BasicSuccessfulResponse(any<String>())

            val result = serviceUnderTest.deleteById(documents.id)

            assertThat(result).isEqualTo("Expert documents deleted successfully.")

            assertThat(user.tutorDocuments).isNull()

            verify { serviceUnderTest.getById(documents.id, any(), any()) }
            verify { usersRepository.save(any()) }
            verify { repository.delete(documents) }
            verify { documentsApiResolver.deleteDocument(any(), any()) }
        }

        @Test
        fun `Should return NotFoundException if documents not found`() {
            val user = createUser().copy(role = UsersRoles.EXPERT)
            val documents = createExpertDocuments(user)

            every {
                serviceUnderTest.getById(
                    documents.id,
                    any(),
                    any()
                )
            } throws NotFoundException("Expert documents not found.")

            val exception = assertFailsWith<NotFoundException> { serviceUnderTest.deleteById(documents.id) }

            assertThat(exception.message).isEqualTo("Expert documents not found.")

            verify { serviceUnderTest.getById(documents.id, any(), any()) }

            verify(exactly = 0) { usersRepository.save(any()) }
            verify(exactly = 0) { repository.delete(documents) }
            verify(exactly = 0) { documentsApiResolver.deleteDocument(any(), any()) }
        }
    }

    @Nested
    inner class DeleteAllTests {
        @Test
        fun `Should delete all documents and return String`() {
            val documents = List(5) { createExpertDocuments(createUser().copy(role = UsersRoles.TUTOR)) }

            every { repository.findAll() } returns documents
            every { serviceUnderTest.deleteById(any()) } returns "Expert documents deleted successfully."

            val result = serviceUnderTest.deleteAll()

            assertThat(result).isEqualTo("All expert documents deleted successfully")

            verify { repository.findAll() }
            verify(exactly = 5) { serviceUnderTest.deleteById(any()) }
        }
    }
}