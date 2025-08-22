package org.careerseekers.userservice.services.userDocumentsService

import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.entities.UserDocuments
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mocks.UserDocumentsServiceMocks
import org.careerseekers.userservice.mocks.generators.DocumentsGenerator.createUserDocs
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUser
import org.junit.jupiter.api.Nested
import java.util.Optional
import kotlin.test.Test
import kotlin.test.assertFailsWith

class UserDocumentsServiceGetTests : UserDocumentsServiceMocks() {

    @Nested
    inner class GetAllTests {

        @Test
        fun `Should return a list of user documents`() {
            val documents = List(5) { createUserDocs(createUser()) }

            every { repository.findAll() } returns documents

            val result = serviceUnderTest.getAll()

            assertThat(result).isEqualTo(documents)
            verify { repository.findAll() }
        }

        @Test
        fun `Should return empty list of user documents`() {
            val documents = emptyList<UserDocuments>()

            every { repository.findAll() } returns documents

            val result = serviceUnderTest.getAll()

            assertThat(result).isEqualTo(documents)
            verify { repository.findAll() }
        }
    }

    @Nested
    inner class GetByIdTests {

        @Test
        fun `GetById should return user document by id`() {
            val documents = createUserDocs(createUser())

            every { repository.findById(documents.id) } returns Optional.of(documents)

            val result = serviceUnderTest.getById(documents.id)

            assertThat(result).isNotNull.isEqualTo(documents)
            verify { repository.findById(documents.id) }
        }

        @Test
        fun `Should return NotFoundException when documents not found and throwable = true`() {
            val documents = createUserDocs(createUser())
            every { repository.findById(any()) } returns Optional.empty()

            val exception = assertFailsWith<NotFoundException> {
                serviceUnderTest.getById(documents.id)
            }

            assertThat(exception.message).isEqualTo("User documents with id ${documents.id} not found")
            verify { repository.findById(any()) }
        }

        @Test
        fun `Should return null when documents not found and throwable = false`() {
            val documents = createUserDocs(createUser())
            every { repository.findById(any()) } returns Optional.empty()

            val result = serviceUnderTest.getById(documents.id, false)

            assertThat(result).isNull()
            verify { repository.findById(any()) }
        }
    }
}