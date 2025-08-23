package org.careerseekers.userservice.services.tutorDocumentsService

import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.entities.TutorDocuments
import org.careerseekers.userservice.mocks.TutorDocumentsServiceMocks
import org.careerseekers.userservice.mocks.generators.DocumentsGenerator.createTutorDocuments
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUser
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class TutorDocumentsServiceGetTests : TutorDocumentsServiceMocks() {

    @Nested
    inner class GetAllTests {

        @Test
        fun `Should return a list of tutor documents`() {
            val documents = List(5) { createTutorDocuments(createUser()) }

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
}