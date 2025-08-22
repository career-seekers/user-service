package org.careerseekers.userservice.services.userDocumentsService

import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.exceptions.BadRequestException
import org.careerseekers.userservice.exceptions.DoubleRecordException
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mocks.UserDocumentsServiceMocks
import org.careerseekers.userservice.mocks.generators.DocumentsGenerator.createUserDocs
import org.careerseekers.userservice.mocks.generators.DocumentsGenerator.createUserDocsDto
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUser
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertFailsWith

class UserDocumentsServiceCreateTests : UserDocumentsServiceMocks() {

    @Nested
    inner class CreateTests {

        @Test
        fun `Should create a user document and return UserDocuments`() {
            val user = createUser().copy(role = UsersRoles.USER)
            val documentsDto = createUserDocsDto(user)
            val documents = createUserDocs(user)

            every { usersService.getById(user.id, any(), any()) } returns user
            every { serviceUnderTest.getDocsByUserId(user.id, throwable = false) } returns null
            every { snilsValidator.checkSnilsValid(any()) } returns Unit
            every { serviceUnderTest["createUserDocument"](documentsDto, user) } returns documents
            every { repository.save(any()) } returns documents

            val result = serviceUnderTest.create(documentsDto)

            assertThat(result).isEqualTo(documents)

            verify { usersService.getById(user.id, any(), any()) }
            verify { serviceUnderTest.getDocsByUserId(user.id, throwable = false) }
            verify { serviceUnderTest["createUserDocument"](documentsDto, user) }
            verify { repository.save(any()) }
        }

        @Test
        fun `Should return NotFoundException when user not found`() {
            val user = createUser().copy(role = UsersRoles.USER)
            val documentsDto = createUserDocsDto(user)

            every {
                usersService.getById(
                    user.id,
                    any(),
                    any()
                )
            } throws NotFoundException("User with id ${user.id} not found")

            val exception = assertFailsWith<NotFoundException> {
                serviceUnderTest.create(documentsDto)
            }

            assertThat(exception.message).isEqualTo("User with id ${user.id} not found")

            verify { usersService.getById(user.id, any(), any()) }

            verify(exactly = 0) { serviceUnderTest.getDocsByUserId(user.id, throwable = false) }
            verify(exactly = 0) { serviceUnderTest["createUserDocument"](documentsDto, user) }
            verify(exactly = 0) { repository.save(any()) }
        }

        @Test
        fun `Should return BadRequestException when user found but users role not USER`() {
            val user = createUser().copy(role = UsersRoles.ADMIN)
            val documentsDto = createUserDocsDto(user)

            every { usersService.getById(user.id, any(), any()) } returns user
            every {
                serviceUnderTest.getDocsByUserId(
                    user.id,
                    throwable = false
                )
            } throws BadRequestException("This user has role ${user.role}, not ${UsersRoles.USER}. Please use another controller to check his documents.")

            val exception = assertFailsWith<BadRequestException> {
                serviceUnderTest.create(documentsDto)
            }

            assertThat(exception.message).isEqualTo("This user has role ${user.role}, not ${UsersRoles.USER}. Please use another controller to check his documents.")

            verify { usersService.getById(user.id, any(), any()) }
            verify { serviceUnderTest.getDocsByUserId(user.id, throwable = false) }

            verify(exactly = 0) { serviceUnderTest["createUserDocument"](documentsDto, user) }
            verify(exactly = 0) { repository.save(any()) }
        }

        @Test
        fun `Should throw DoubleRecordException when user found and user docs found too`() {
            val user = createUser().copy(role = UsersRoles.USER)
            val documents = createUserDocs(user)
            val documentsDto = createUserDocsDto(user)

            every { usersService.getById(user.id, any(), any()) } returns user
            every { serviceUnderTest.getDocsByUserId(user.id, throwable = false) } returns documents

            val exception = assertFailsWith<DoubleRecordException> {
                serviceUnderTest.create(documentsDto)
            }

            assertThat(exception.message).isEqualTo("This user already has documents. If you want to change it, use update method.")

            verify { usersService.getById(user.id, any(), any()) }
            verify { serviceUnderTest.getDocsByUserId(user.id, throwable = false) }

            verify(exactly = 0) { serviceUnderTest["createUserDocument"](documentsDto, user) }
            verify(exactly = 0) { repository.save(any()) }
        }

        @Test
        fun `Should return BadRequestException if snils number invalid (not enough chars)`() {
            val user = createUser().copy(role = UsersRoles.USER)
            val documentsDto = createUserDocsDto(user)

            every { usersService.getById(user.id, any(), any()) } returns user
            every { serviceUnderTest.getDocsByUserId(user.id, throwable = false) } returns null
            every { snilsValidator.checkSnilsValid(any()) } returns Unit
            every {
                serviceUnderTest["createUserDocument"](
                    documentsDto,
                    user
                )
            } throws BadRequestException("Snils number length must be 11 chars")

            val exception = assertFailsWith<BadRequestException> { serviceUnderTest.create(documentsDto) }

            assertThat(exception.message).isEqualTo("Snils number length must be 11 chars")

            verify { usersService.getById(user.id, any(), any()) }
            verify { serviceUnderTest.getDocsByUserId(user.id, throwable = false) }
            verify { serviceUnderTest["createUserDocument"](documentsDto, user) }

            verify(exactly = 0) { repository.save(any()) }
        }

        @Test
        fun `Should return BadRequestException if snils number invalid (chars is not digits)`() {
            val user = createUser().copy(role = UsersRoles.USER)
            val documentsDto = createUserDocsDto(user)

            every { usersService.getById(user.id, any(), any()) } returns user
            every { serviceUnderTest.getDocsByUserId(user.id, throwable = false) } returns null
            every { snilsValidator.checkSnilsValid(any()) } returns Unit
            every {
                serviceUnderTest["createUserDocument"](
                    documentsDto,
                    user
                )
            } throws BadRequestException("Snils number must be a digit")

            val exception = assertFailsWith<BadRequestException> { serviceUnderTest.create(documentsDto) }

            assertThat(exception.message).isEqualTo("Snils number must be a digit")

            verify { usersService.getById(user.id, any(), any()) }
            verify { serviceUnderTest.getDocsByUserId(user.id, throwable = false) }
            verify { serviceUnderTest["createUserDocument"](documentsDto, user) }

            verify(exactly = 0) { repository.save(any()) }
        }

        @Test
        fun `Should return DoubleRecordException when user with same snils number already exists`() {
            val user = createUser().copy(role = UsersRoles.USER)
            val documentsDto = createUserDocsDto(user)

            every { usersService.getById(user.id, any(), any()) } returns user
            every { serviceUnderTest.getDocsByUserId(user.id, throwable = false) } returns null
            every { snilsValidator.checkSnilsValid(any()) } returns Unit
            every {
                serviceUnderTest["createUserDocument"](
                    documentsDto,
                    user
                )
            } throws DoubleRecordException("Documents with snils number ${documentsDto.snilsDto.snilsNumber} already exists")

            val exception = assertFailsWith<DoubleRecordException> { serviceUnderTest.create(documentsDto) }

            assertThat(exception.message).isEqualTo("Documents with snils number ${documentsDto.snilsDto.snilsNumber} already exists")

            verify { usersService.getById(user.id, any(), any()) }
            verify { serviceUnderTest.getDocsByUserId(user.id, throwable = false) }
            verify { serviceUnderTest["createUserDocument"](documentsDto, user) }

            verify(exactly = 0) { repository.save(any()) }
        }
    }
}