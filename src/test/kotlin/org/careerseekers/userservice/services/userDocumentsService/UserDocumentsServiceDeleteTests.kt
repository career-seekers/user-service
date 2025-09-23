package org.careerseekers.userservice.services.userDocumentsService

import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mocks.UserDocumentsServiceMocks
import org.careerseekers.userservice.mocks.generators.DocumentsGenerator.createUserDocs
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUser
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertFailsWith

class UserDocumentsServiceDeleteTests : UserDocumentsServiceMocks() {

    @Nested
    inner class DeleteByIdTests {

        @Test
        fun `Should set userDocs to null and delete it`() {
            val user = createUser()
            val documents = createUserDocs(user)

            every { serviceUnderTest.getById(documents.id, true) } returns documents
            every { usersRepository.save(any()) } returns user
            every { repository.delete(any()) } returns Unit
            every { serviceUnderTest["removeDocumentsFromDatabase"](documents) } returns Unit

            val result = serviceUnderTest.deleteById(documents.id)

            assertThat(result).isEqualTo("Пользовательские документы успешно удалены.")
            assertThat(user.userDocuments).isNull()

            verify { serviceUnderTest.getById(documents.id, true) }
            verify { usersRepository.save(any()) }
            verify { repository.delete(any()) }
            verify { serviceUnderTest["removeDocumentsFromDatabase"](documents) }
        }

        @Test
        fun `Should return NotFoundException when user documents not found`() {
            val user = createUser()
            val documents = createUserDocs(user)

            every { serviceUnderTest.getById(documents.id, true) } throws NotFoundException("User documents with id ${documents.id} not found")

            val exception = assertFailsWith<NotFoundException> {
                serviceUnderTest.deleteById(documents.id)
            }

            assertThat(exception.message).isEqualTo("User documents with id ${documents.id} not found")

            verify { serviceUnderTest.getById(documents.id, true) }

            verify(exactly = 0) { usersRepository.save(any()) }
            verify(exactly = 0) { repository.delete(any()) }
            verify(exactly = 0) { serviceUnderTest["removeDocumentsFromDatabase"](documents) }
        }
    }

    @Nested
    inner class DeleteAllTests {
        @Test
        fun `Should return 5 users`() {
            val documents = List(5) { createUserDocs(createUser()) }

            every { serviceUnderTest.getAll() } returns documents
            every { serviceUnderTest.deleteById(any()) } returns "Пользовательские документы успешно удалены."

            val result = serviceUnderTest.deleteAll()

            assertThat(result).isEqualTo("Все документы пользователей успешно удалены.")

            verify { serviceUnderTest.getAll() }
            verify(exactly = 5) { serviceUnderTest.deleteById(any()) }
        }
    }
}