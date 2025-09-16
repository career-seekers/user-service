package org.careerseekers.userservice.services.tutorDocumentsService

import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.dto.docs.UpdateTutorDocsDto
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mocks.TutorDocumentsServiceMocks
import org.careerseekers.userservice.mocks.generators.DocumentsGenerator.createTutorDocuments
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUser
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertFailsWith

class TutorDocumentsServiceUpdateTests : TutorDocumentsServiceMocks() {

    @Nested
    inner class UpdateTests {

        private val user = createUser().copy(role = UsersRoles.TUTOR)
        private val documents = createTutorDocuments(user).copy(post = "oldPost")
        private val dto = UpdateTutorDocsDto(
            id = documents.id,
            institution = "newInstitution",
            post = null,
        )

        @Test
        fun `Should update documents and return String`() {
            every { serviceUnderTest.getById(dto.id, any(), any()) } returns documents

            val result = serviceUnderTest.update(dto)

            assertThat(result).isEqualTo("Tutor documents updated successfully.")

            assertThat(documents.institution).isEqualTo("newInstitution")
            assertThat(documents.post).isEqualTo("oldPost")

            verify { serviceUnderTest.getById(dto.id, any(), any()) }
        }

        @Test
        fun `Should return NotFoundException when documents not found`() {
            every { serviceUnderTest.getById(dto.id, any(), any()) } throws NotFoundException("Tutor documents not found.")

            val exception = assertFailsWith<NotFoundException> {
                serviceUnderTest.update(dto)
            }

            assertThat(exception.message).isEqualTo("Tutor documents not found.")

            verify { serviceUnderTest.getById(dto.id, any(), any()) }
        }
    }
}