package org.careerseekers.userservice.services.userDocumentsService

import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.entities.UserDocuments
import org.careerseekers.userservice.mocks.UserDocumentsServiceMocks
import org.careerseekers.userservice.mocks.generators.DocumentsGenerator.createUserDocs
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUser
import org.junit.jupiter.api.Nested
import kotlin.test.Test

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
}