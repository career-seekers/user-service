package org.careerseekers.userservice.services.expertDocumentsService

import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.dto.docs.UpdateExpertDocsDto
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mocks.ExpertDocumentsServiceMocks
import org.careerseekers.userservice.mocks.generators.DocumentsGenerator.createExpertDocuments
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUser
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ExpertDocumentsServiceUpdateTests : ExpertDocumentsServiceMocks() {

    @Nested
    inner class UpdateTests {

        private val user = createUser().copy(role = UsersRoles.EXPERT)
        private val documents = createExpertDocuments(user).copy(post = "oldPost")
        private val dto = UpdateExpertDocsDto(
            id = documents.id,
            institution = "newInstitution",
            post = null,
        )

        @Test
        fun `Should update documents and return String`() {
            every { serviceUnderTest.getById(dto.id, any(), any()) } returns documents

            val result = serviceUnderTest.update(dto)

            assertThat(result).isEqualTo("Expert documents updated successfully.")

            assertThat(documents.institution).isEqualTo("newInstitution")
            assertThat(documents.post).isEqualTo("oldPost")

            verify { serviceUnderTest.getById(dto.id, any(), any()) }
        }

        @Test
        fun `Should return NotFoundException when documents not found`() {
            every { serviceUnderTest.getById(dto.id, any(), any()) } throws NotFoundException("Expert documents not found.")

            val exception = assertFailsWith<NotFoundException> {
                serviceUnderTest.update(dto)
            }

            assertThat(exception.message).isEqualTo("Expert documents not found.")

            verify { serviceUnderTest.getById(dto.id, any(), any()) }
        }
    }
}