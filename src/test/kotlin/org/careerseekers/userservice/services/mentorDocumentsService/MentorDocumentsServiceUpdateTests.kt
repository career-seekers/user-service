package org.careerseekers.userservice.services.mentorDocumentsService

import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.dto.docs.UpdateMentorDocsDto
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.careerseekers.userservice.mocks.MentorDocumentsServiceMocks
import org.careerseekers.userservice.mocks.generators.DocumentsGenerator.createMentorDocuments
import org.careerseekers.userservice.mocks.generators.MultipartFileGenerator.createMultipartFile
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUser
import org.junit.jupiter.api.Nested
import org.mockito.ArgumentMatchers.any
import kotlin.test.Test
import kotlin.test.assertFailsWith

class MentorDocumentsServiceUpdateTests : MentorDocumentsServiceMocks() {

    @Nested
    inner class UpdateTests {

        private val user = createUser().copy(role = UsersRoles.MENTOR)
        private val documents = createMentorDocuments(user).copy(post = "oldPost")
        private val dto = UpdateMentorDocsDto(
            id = documents.id,
            institution = "newInstitution",
            post = null,
            consentToMentorPdp = createMultipartFile()
        )

        @Test
        fun `Should update documents and return String`() {


            every { serviceUnderTest.getById(dto.id, any(), any()) } returns documents
            every { documentsApiResolver.loadDocId(any(), dto.consentToMentorPdp) } returns 1234567L
            every { documentsApiResolver.deleteDocument(any(), false) } returns BasicSuccessfulResponse(any<String>())

            val result = serviceUnderTest.update(dto)

            assertThat(result).isEqualTo("Документы наставника обновлены успешно.")

            assertThat(documents.institution).isEqualTo("newInstitution")
            assertThat(documents.post).isEqualTo("oldPost")
            assertThat(documents.consentToMentorPdpId).isEqualTo(1234567L)

            verify { serviceUnderTest.getById(dto.id, any(), any()) }
            verify { documentsApiResolver.loadDocId(any(), dto.consentToMentorPdp) }
            verify { documentsApiResolver.deleteDocument(any(), false) }
        }

        @Test
        fun `Should return NotFoundException when documents not found`() {
            every { serviceUnderTest.getById(dto.id, any(), any()) } throws NotFoundException("Документы наставника не найдены.")

            val exception = assertFailsWith<NotFoundException> {
                serviceUnderTest.update(dto)
            }

            assertThat(exception.message).isEqualTo("Документы наставника не найдены.")

            verify { serviceUnderTest.getById(dto.id, any(), any()) }

            verify(exactly = 0) { documentsApiResolver.loadDocId(any(), dto.consentToMentorPdp) }
            verify(exactly = 0) { documentsApiResolver.deleteDocument(any(), false) }
        }
    }
}