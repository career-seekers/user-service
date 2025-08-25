package org.careerseekers.userservice.services.userDocumentsService

import org.careerseekers.userservice.mocks.generators.MocksGenerator.randomString
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.dto.docs.UpdateUserDocsDto
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.careerseekers.userservice.io.converters.extensions.checkNullable
import org.careerseekers.userservice.mocks.UserDocumentsServiceMocks
import org.careerseekers.userservice.mocks.generators.DocumentsGenerator.createUserDocs
import org.careerseekers.userservice.mocks.generators.MultipartFileGenerator.createMultipartFile
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.mockito.ArgumentMatchers.any
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertFailsWith

class UserDocumentsServiceUpdateTests : UserDocumentsServiceMocks() {

    @Nested
    inner class UpdateTests {

        val user = createUser()
        val documents = createUserDocs(user)
        val dto = UpdateUserDocsDto(
            id = documents.id,
            snilsNumber = "123456",
            snilsFile = createMultipartFile(),
            studyingPlace = randomString(12),
            studyingCertificateFile = createMultipartFile(),
            learningClass = Random.nextLong(1, 11).toShort(),
            trainingGround = randomString(12),
            additionalStudyingCertificateFile = createMultipartFile(),
            parentRole = randomString(12),
            consentToChildPdpFile = createMultipartFile()
        )

        @BeforeEach
        fun setup() {
            mockkStatic("org.careerseekers.userservice.io.converters.extensions.ListNullableCheckerKt")
        }

        @Test
        fun `Should update user documents`() {
            val list = listOf(dto.snilsNumber, dto.snilsFile)

            every { list.checkNullable(any()) } just Runs
            every { serviceUnderTest.getById(dto.id, true) } returns documents
            every { snilsValidator.checkSnilsValid(any()) } returns Unit
            every { documentsApiResolver.loadDocId(any(), any()) } returns 123456789
            every { documentsApiResolver.deleteDocument(any(), throwable = false) } returns BasicSuccessfulResponse(any<String>())

            val result = serviceUnderTest.update(dto)

            assertThat(result).isNotNull.isEqualTo("User documents updated successfully.")

            assertThat(documents.id).isEqualTo(dto.id)
            assertThat(documents.user).isEqualTo(user)
            assertThat(documents.snilsNumber).isEqualTo(dto.snilsNumber)
            assertThat(documents.snilsId).isEqualTo(123456789)
            assertThat(documents.studyingPlace).isEqualTo(dto.studyingPlace)
            assertThat(documents.studyingCertificateId).isEqualTo(123456789)
            assertThat(documents.learningClass).isEqualTo(dto.learningClass)
            assertThat(documents.trainingGround).isEqualTo(dto.trainingGround)
            assertThat(documents.additionalStudyingCertificateId).isEqualTo(123456789)
            assertThat(documents.consentToChildPdpId).isEqualTo(123456789)
        }

        @Test
        fun `Should return NotFoundException when user documents not found`() {
            val list = listOf(dto.snilsNumber, dto.snilsFile)

            every { serviceUnderTest.getById(dto.id, true) } throws NotFoundException("User documents with id ${dto.id} not found")

            val exception = assertFailsWith<NotFoundException> { serviceUnderTest.update(dto) }

            assertThat(exception.message).isEqualTo("User documents with id ${dto.id} not found")

            verify { serviceUnderTest.getById(dto.id, true) }

            verify(exactly = 0) { list.checkNullable(any()) }
            verify(exactly = 0) { snilsValidator.checkSnilsValid(any()) }
            verify(exactly = 0) { documentsApiResolver.loadDocId(any(), any()) }
            verify(exactly = 0) { documentsApiResolver.deleteDocument(any(), throwable = false) }
        }
    }
}