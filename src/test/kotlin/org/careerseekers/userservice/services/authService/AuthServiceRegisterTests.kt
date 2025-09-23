package org.careerseekers.userservice.services.authService

import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.mocks.generators.RegistrationsDtoGenerator.createUserRegistrationDto
import org.careerseekers.userservice.mocks.generators.RegistrationsDtoGenerator.createUserWithChildRegistrationDto
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUser
import org.careerseekers.userservice.dto.jwt.UserTokensDto
import org.careerseekers.userservice.enums.MailEventTypes
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.exceptions.BadRequestException
import org.careerseekers.userservice.mocks.AuthServiceMocks
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertFailsWith

class AuthServiceRegisterTests : AuthServiceMocks() {

    @Nested
    inner class RegisterTests {
        private val user = createUser()

        @BeforeEach
        fun setup() {
            every { userPostProcessor.userRole } returns UsersRoles.USER
            every { expertPostProcessor.userRole } returns UsersRoles.EXPERT
        }

        @Test
        fun `register should return UserTokensDto`() {
            val regDto = createUserRegistrationDto(UsersRoles.TUTOR)

            every { emailVerificationCodeVerifier.verify(any(), any(), any(), any()) } returns Unit
            every { verificationCodesCacheClient.deleteItemFromCache(any()) } returns Unit

            every { usersService.create(any()) } returns user
            every { jwtUtil.removeOldRefreshTokenByUUID(regDto.uuid) } returns Unit
            every { jwtUtil.generateAccessToken(any()) } returns "accessToken"
            every { jwtUtil.generateRefreshToken(any()) } returns "refreshToken"

            val result = serviceUnderTest.register(regDto)

            assertThat(result).isEqualTo(UserTokensDto(accessToken = "accessToken", refreshToken = "refreshToken"))

            verify {
                emailVerificationCodeVerifier.verify(
                    email = regDto.email,
                    verificationCode = regDto.verificationCode,
                    mailEventTypes = MailEventTypes.PRE_REGISTRATION
                )
            }
            verify { usersService.create(any()) }
            verify { jwtUtil.removeOldRefreshTokenByUUID(any()) }
            verify { jwtUtil.generateAccessToken(any()) }
            verify { jwtUtil.generateRefreshToken(any()) }

            verify(exactly = 0) { userPostProcessor.processRegistration(any()) }
            verify(exactly = 0) { expertPostProcessor.processRegistration(any()) }
        }

        @Test
        fun `register should return UserTokensDto and use UsersRegistrationProcessor`() {
            val regDto = createUserWithChildRegistrationDto(UsersRoles.USER)

            every { emailVerificationCodeVerifier.verify(any(), any(), any(), any()) } returns Unit
            every { verificationCodesCacheClient.deleteItemFromCache(any()) } returns Unit

            every { usersService.create(any()) } returns user
            every { jwtUtil.removeOldRefreshTokenByUUID(regDto.uuid) } returns Unit
            every { jwtUtil.generateAccessToken(any()) } returns "accessToken"
            every { jwtUtil.generateRefreshToken(any()) } returns "refreshToken"
            every { userPostProcessor.processRegistration(any()) } returns Unit

            val result = serviceUnderTest.register(regDto)

            assertThat(result).isEqualTo(UserTokensDto(accessToken = "accessToken", refreshToken = "refreshToken"))

            verify {
                emailVerificationCodeVerifier.verify(
                    email = regDto.email,
                    verificationCode = regDto.verificationCode,
                    mailEventTypes = MailEventTypes.PRE_REGISTRATION
                )
            }
            verify { usersService.create(any()) }
            verify { jwtUtil.removeOldRefreshTokenByUUID(any()) }
            verify { jwtUtil.generateAccessToken(any()) }
            verify { jwtUtil.generateRefreshToken(any()) }
            verify { userPostProcessor.processRegistration(any()) }

            verify(exactly = 0) { expertPostProcessor.processRegistration(any()) }
        }

        @Test
        fun `register should return BadRequest exception if try to create user (USER) without child`() {
            val regDto = createUserRegistrationDto(UsersRoles.USER)

            every { emailVerificationCodeVerifier.verify(any(), any(), any(), any()) } returns Unit
            every { verificationCodesCacheClient.deleteItemFromCache(any()) } returns Unit

            every { usersService.create(any()) } returns user
            every { jwtUtil.removeOldRefreshTokenByUUID(regDto.uuid) } returns Unit
            every { jwtUtil.generateAccessToken(any()) } returns "accessToken"
            every { jwtUtil.generateRefreshToken(any()) } returns "refreshToken"
            every { userPostProcessor.processRegistration(regDto) } throws BadRequestException("Неверный пакет данных для регистрации пользователя.")

            val exception = assertFailsWith<BadRequestException> {
                serviceUnderTest.register(regDto)
            }

            assertThat(exception.message).isEqualTo("Неверный пакет данных для регистрации пользователя.")

            verify {
                emailVerificationCodeVerifier.verify(
                    email = regDto.email,
                    verificationCode = regDto.verificationCode,
                    mailEventTypes = MailEventTypes.PRE_REGISTRATION
                )
            }
            verify { usersService.create(any()) }
            verify { jwtUtil.removeOldRefreshTokenByUUID(any()) }
            verify { jwtUtil.generateAccessToken(any()) }
            verify { jwtUtil.generateRefreshToken(any()) }
            verify { userPostProcessor.processRegistration(any()) }

            verify(exactly = 0) { expertPostProcessor.processRegistration(any()) }
        }

        @Test
        fun `register should return UserTokensDto and use ExpertRegistrationProcessor`() {
            val regDto = createUserRegistrationDto(UsersRoles.EXPERT)

            every { emailVerificationCodeVerifier.verify(any(), any(), any(), any()) } returns Unit
            every { verificationCodesCacheClient.deleteItemFromCache(any()) } returns Unit

            every { usersService.create(any()) } returns user
            every { jwtUtil.removeOldRefreshTokenByUUID(regDto.uuid) } returns Unit
            every { jwtUtil.generateAccessToken(any()) } returns "accessToken"
            every { jwtUtil.generateRefreshToken(any()) } returns "refreshToken"
            every { expertPostProcessor.processRegistration(any()) } returns Unit

            val result = serviceUnderTest.register(regDto)

            assertThat(result).isEqualTo(UserTokensDto(accessToken = "accessToken", refreshToken = "refreshToken"))

            verify {
                emailVerificationCodeVerifier.verify(
                    email = regDto.email,
                    verificationCode = regDto.verificationCode,
                    mailEventTypes = MailEventTypes.PRE_REGISTRATION
                )
            }
            verify { usersService.create(any()) }
            verify { jwtUtil.removeOldRefreshTokenByUUID(any()) }
            verify { jwtUtil.generateAccessToken(any()) }
            verify { jwtUtil.generateRefreshToken(any()) }
            verify { expertPostProcessor.processRegistration(any()) }

            verify(exactly = 0) { userPostProcessor.processRegistration(any()) }

        }

        @Test
        fun `register should return BadRequestException if verification code not found (expired)`() {
            val regDto = createUserRegistrationDto(UsersRoles.TUTOR)

            every {
                emailVerificationCodeVerifier.verify(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } throws BadRequestException("Верификационный код не найден. Повторите попытку позже.")

            val exception = assertFailsWith<BadRequestException> {
                serviceUnderTest.register(regDto)
            }

            assertThat(exception.message).isEqualTo("Верификационный код не найден. Повторите попытку позже.")

            verify {
                emailVerificationCodeVerifier.verify(
                    email = regDto.email,
                    verificationCode = regDto.verificationCode,
                    mailEventTypes = MailEventTypes.PRE_REGISTRATION
                )
            }

            verify(exactly = 0) { usersService.create(any()) }
            verify(exactly = 0) { jwtUtil.removeOldRefreshTokenByUUID(any()) }
            verify(exactly = 0) { jwtUtil.generateAccessToken(any()) }
            verify(exactly = 0) { jwtUtil.generateRefreshToken(any()) }
            verify(exactly = 0) { userPostProcessor.processRegistration(any()) }
            verify(exactly = 0) { expertPostProcessor.processRegistration(any()) }
        }

        @Test
        fun `register should return BadRequestException if verification code incorrect`() {
            val regDto = createUserRegistrationDto(UsersRoles.TUTOR)

            every {
                emailVerificationCodeVerifier.verify(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } throws BadRequestException("Неверный верификационный код.")

            val exception = assertFailsWith<BadRequestException> {
                serviceUnderTest.register(regDto)
            }

            assertThat(exception.message).isEqualTo("Неверный верификационный код.")

            verify {
                emailVerificationCodeVerifier.verify(
                    email = regDto.email,
                    verificationCode = regDto.verificationCode,
                    mailEventTypes = MailEventTypes.PRE_REGISTRATION
                )
            }

            verify(exactly = 0) { usersService.create(any()) }
            verify(exactly = 0) { jwtUtil.removeOldRefreshTokenByUUID(any()) }
            verify(exactly = 0) { jwtUtil.generateAccessToken(any()) }
            verify(exactly = 0) { jwtUtil.generateRefreshToken(any()) }
            verify(exactly = 0) { userPostProcessor.processRegistration(any()) }
            verify(exactly = 0) { expertPostProcessor.processRegistration(any()) }
        }

        @Test
        fun `register should return BadRequestException if verification code incorrect and maximum count of attempts reached`() {
            val regDto = createUserRegistrationDto(UsersRoles.TUTOR)

            every {
                emailVerificationCodeVerifier.verify(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } throws BadRequestException("Достигнуто максимальное количество попыток. На почту отправлен новый код.")

            val exception = assertFailsWith<BadRequestException> {
                serviceUnderTest.register(regDto)
            }

            assertThat(exception.message).isEqualTo("Достигнуто максимальное количество попыток. На почту отправлен новый код.")

            verify {
                emailVerificationCodeVerifier.verify(
                    email = regDto.email,
                    verificationCode = regDto.verificationCode,
                    mailEventTypes = MailEventTypes.PRE_REGISTRATION
                )
            }

            verify(exactly = 0) { usersService.create(any()) }
            verify(exactly = 0) { jwtUtil.removeOldRefreshTokenByUUID(any()) }
            verify(exactly = 0) { jwtUtil.generateAccessToken(any()) }
            verify(exactly = 0) { jwtUtil.generateRefreshToken(any()) }
            verify(exactly = 0) { userPostProcessor.processRegistration(any()) }
            verify(exactly = 0) { expertPostProcessor.processRegistration(any()) }
        }
    }
}