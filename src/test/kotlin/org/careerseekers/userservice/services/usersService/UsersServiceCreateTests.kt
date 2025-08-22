package org.careerseekers.userservice.services.usersService

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.mocks.generators.FileStructureGenerator.createFileStructure
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUser
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUserDto
import org.careerseekers.userservice.enums.FileTypes
import org.careerseekers.userservice.exceptions.DoubleRecordException
import org.careerseekers.userservice.exceptions.MobileNumberFormatException
import org.careerseekers.userservice.mocks.UsersServiceMocks
import org.careerseekers.userservice.utils.MobileNumberFormatter
import org.careerseekers.userservice.utils.MobileNumberFormatter.checkMobileNumberValid
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(MockKExtension::class)
class UsersServiceCreateTests : UsersServiceMocks() {

    private val defaultAvatarId = "42"
    private fun initDefaultAvatarId() {
        val defaultAvatarFieldUsersService = serviceUnderTest.javaClass.getDeclaredField("defaultAvatarId")
        defaultAvatarFieldUsersService.isAccessible = true
        defaultAvatarFieldUsersService.set(serviceUnderTest, defaultAvatarId)

        val defaultAvatarFieldusersServiceMock = usersServiceMock.javaClass.getDeclaredField("defaultAvatarId")
        defaultAvatarFieldusersServiceMock.isAccessible = true
        defaultAvatarFieldusersServiceMock.set(usersServiceMock, defaultAvatarId)
    }

    @BeforeEach
    fun setup() {
        initDefaultAvatarId()

        mockkObject(MobileNumberFormatter)
        every { checkMobileNumberValid(any()) } just runs
    }

    @Nested
    inner class CreateTests {

        @Test
        fun `create should save user when avatarId is null`() {
            val user = createUser()
            val dto = createUserDto(user).copy(avatarId = null, password = "encodedPassword")

            every {
                usersServiceMock invoke "checkIfUserExistsByEmailOrMobile" withArguments listOf(
                    dto.email,
                    dto.mobileNumber
                )
            } returns Unit
            every { passwordEncoder.encode(dto.password) } returns "encodedPassword"
            every { usersMapper.usersFromCreateDto(any()) } returns user
            every { repository.save(user) } returns user

            val result = serviceUnderTest.create(dto)

            assertThat(result).isNotNull().isEqualTo(user)

            verify(exactly = 0) { documentExistenceChecker.checkFileExistence(any(), any()) }
            verify { passwordEncoder.encode(dto.password) }
            verify { usersMapper.usersFromCreateDto(any()) }
            verify { repository.save(user) }
        }

        @Test
        fun `create should check avatar existence when avatarId is not null and differs from default`() {
            val user = createUser()
            val dto = createUserDto(user).copy(password = "encodedPassword", avatarId = 1)
            val fileStructure = createFileStructure(FileTypes.AVATAR)

            every {
                documentExistenceChecker.checkFileExistence(
                    dto.avatarId!!,
                    FileTypes.AVATAR
                )
            } returns fileStructure
            every {
                usersServiceMock invoke "checkIfUserExistsByEmailOrMobile" withArguments listOf(
                    dto.email,
                    dto.mobileNumber
                )
            } returns Unit
            every { passwordEncoder.encode(dto.password) } returns "encodedPassword"
            every { usersMapper.usersFromCreateDto(any()) } returns user
            every { repository.save(user) } returns user

            val result = serviceUnderTest.create(dto)

            assertThat(result).isNotNull().isEqualTo(user)

            verify { documentExistenceChecker.checkFileExistence(any(), any()) }
            verify { passwordEncoder.encode(dto.password) }
            verify { usersMapper.usersFromCreateDto(any()) }
            verify { repository.save(user) }
        }

        @Test
        fun `create should throw DoubleRecordException if email already exists`() {
            val user = createUser()
            val dto = createUserDto(user).copy(password = "encodedPassword")
            val fileStructure = createFileStructure(FileTypes.AVATAR)

            every {
                documentExistenceChecker.checkFileExistence(
                    dto.avatarId!!,
                    FileTypes.AVATAR
                )
            } returns fileStructure
            every {
                usersServiceMock invoke "checkIfUserExistsByEmailOrMobile" withArguments listOf(
                    dto.email,
                    dto.mobileNumber
                )
            } throws DoubleRecordException("User with email ${dto.email} already exists")

            every { passwordEncoder.encode(any()) } returns "encodedPassword"

            val exception = assertThrows<DoubleRecordException> {
                usersServiceMock.create(dto)
            }

            assertThat(exception.message).isEqualTo("User with email ${dto.email} already exists")

            verify {
                documentExistenceChecker.checkFileExistence(dto.avatarId!!, FileTypes.AVATAR)
            }
            verify(exactly = 0) { checkMobileNumberValid(any()) }
            verify(exactly = 0) { usersMapper.usersFromCreateDto(any()) }
            verify(exactly = 0) { repository.save(any()) }
        }


        @Test
        fun `create should throw DoubleRecordException if mobile number already exists`() {
            val user = createUser()
            val dto = createUserDto(user).copy(password = "encodedPassword")
            val fileStructure = createFileStructure(FileTypes.AVATAR)

            every {
                documentExistenceChecker.checkFileExistence(
                    dto.avatarId!!,
                    FileTypes.AVATAR
                )
            } returns fileStructure
            every {
                usersServiceMock invoke "checkIfUserExistsByEmailOrMobile" withArguments listOf(
                    dto.email,
                    dto.mobileNumber
                )
            } throws DoubleRecordException("User with mobile number ${dto.mobileNumber} already exists")

            every { passwordEncoder.encode(any()) } returns "encodedPassword"

            val exception = assertThrows<DoubleRecordException> {
                usersServiceMock.create(dto)
            }

            assertThat(exception.message).isEqualTo("User with mobile number ${dto.mobileNumber} already exists")

            verify { documentExistenceChecker.checkFileExistence(dto.avatarId!!, FileTypes.AVATAR) }
            verify(exactly = 0) { checkMobileNumberValid(any()) }
            verify(exactly = 0) { usersMapper.usersFromCreateDto(any()) }
            verify(exactly = 0) { repository.save(any()) }
        }

        @Test
        fun `create should throw MobileNumberFormatException on invalid mobile number`() {
            val user = createUser()
            val dto = createUserDto(user).copy(password = "encodedPassword", mobileNumber = "Wrong mobile number")
            val fileStructure = createFileStructure(FileTypes.AVATAR)

            every {
                documentExistenceChecker.checkFileExistence(
                    dto.avatarId!!,
                    FileTypes.AVATAR
                )
            } returns fileStructure
            every {
                usersServiceMock invoke "checkIfUserExistsByEmailOrMobile" withArguments listOf(
                    dto.email,
                    dto.mobileNumber
                )
            } returns Unit
            every { checkMobileNumberValid(dto.mobileNumber) } throws MobileNumberFormatException("Mobile number must be 12 characters long in format '+79991234567'")
            every { passwordEncoder.encode(any()) } returns "encodedPassword"
            every { usersMapper.usersFromCreateDto(any()) } returns user
            every { repository.save(user) } returns user


            val exception = assertThrows<MobileNumberFormatException> {
                usersServiceMock.create(dto)
            }

            assertThat(exception.message).isEqualTo("Mobile number must be 12 characters long in format '+79991234567'")

            verify { documentExistenceChecker.checkFileExistence(dto.avatarId!!, FileTypes.AVATAR) }
            verify { usersServiceMock invoke "checkIfUserExistsByEmailOrMobile" withArguments listOf(dto.email, dto.mobileNumber) }
            verify { checkMobileNumberValid(any()) }
            verify(exactly = 0) { usersMapper.usersFromCreateDto(any()) }
            verify(exactly = 0) { repository.save(any()) }
        }
    }
}
