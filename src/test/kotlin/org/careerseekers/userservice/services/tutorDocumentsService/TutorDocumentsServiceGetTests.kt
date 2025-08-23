package org.careerseekers.userservice.services.tutorDocumentsService

import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.entities.TutorDocuments
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mocks.TutorDocumentsServiceMocks
import org.careerseekers.userservice.mocks.generators.DocumentsGenerator.createTutorDocuments
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUser
import org.junit.jupiter.api.Nested
import java.util.Optional
import kotlin.test.Test
import kotlin.test.assertFailsWith

class TutorDocumentsServiceGetTests : TutorDocumentsServiceMocks() {

    @Nested
    inner class GetAllTests {

        @Test
        fun `Should return a list of tutor documents`() {
            val documents = List(5) { createTutorDocuments(createUser().copy(role = UsersRoles.TUTOR)) }

            every { repository.findAll() } returns documents

            val result = serviceUnderTest.getAll()

            assertThat(result).isEqualTo(documents)
            verify { repository.findAll() }
        }

        @Test
        fun `Should return empty list of tutor documents`() {
            val documents = emptyList<TutorDocuments>()

            every { repository.findAll() } returns documents

            val result = serviceUnderTest.getAll()

            assertThat(result).isEqualTo(documents)
            verify { repository.findAll() }
        }
    }

    @Nested
    inner class GetByIdTests {

        @Test
        fun `Should return tutor document by id`() {
            val documents = createTutorDocuments(createUser().copy(role = UsersRoles.TUTOR))

            every { repository.findById(documents.id) } returns Optional.of(documents)

            val result = serviceUnderTest.getById(documents.id)

            assertThat(result).isNotNull.isEqualTo(documents)
            verify { repository.findById(documents.id) }
        }

        @Test
        fun `Should return NotFoundException when documents not found and throwable = true`() {
            val documents = createTutorDocuments(createUser().copy(role = UsersRoles.TUTOR))
            every { repository.findById(any()) } returns Optional.empty()

            val exception = assertFailsWith<NotFoundException> {
                serviceUnderTest.getById(documents.id, message = "Tutor documents with id ${documents.id} not found")
            }

            assertThat(exception.message).isEqualTo("Tutor documents with id ${documents.id} not found")
            verify { repository.findById(any()) }
        }

        @Test
        fun `Should return null when documents not found and throwable = false`() {
            val documents = createTutorDocuments(createUser().copy(role = UsersRoles.TUTOR))
            every { repository.findById(any()) } returns Optional.empty()

            val result = serviceUnderTest.getById(documents.id, false)

            assertThat(result).isNull()
            verify { repository.findById(any()) }
        }
    }
}