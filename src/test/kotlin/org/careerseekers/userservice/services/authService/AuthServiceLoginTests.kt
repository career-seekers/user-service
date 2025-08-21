package org.careerseekers.userservice.services.authService

import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.UsersCreator.createUser
import org.careerseekers.userservice.dto.auth.LoginUserDto
import org.careerseekers.userservice.dto.jwt.UserTokensDto
import org.careerseekers.userservice.exceptions.JwtAuthenticationException
import org.careerseekers.userservice.mocks.AuthServiceMocks
import org.junit.jupiter.api.Nested
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertFailsWith

class AuthServiceLoginTests : AuthServiceMocks() {

    @Nested
    inner class LoginTests {
        private val user = createUser()
        private val loginDto = LoginUserDto(user.email, user.password, UUID.randomUUID())

        @Test
        fun `login should return UserTokensDto`() {
            every { usersService.getByEmail(user.email) } returns user
            every { jwtUtil.removeOldRefreshTokenByUUID(loginDto.uuid) } returns Unit
            every { passwordEncoder.matches(loginDto.password, user.password) } returns true
            every { jwtUtil.generateAccessToken(any()) } returns "accessToken"
            every { jwtUtil.generateRefreshToken(any()) } returns "refreshToken"

            val result = serviceUnderTest.login(loginDto)

            assertThat(result).isNotNull.isEqualTo(UserTokensDto(
                accessToken = "accessToken",
                refreshToken = "refreshToken"
            ))

            verify { usersService.getByEmail(user.email) }
            verify { jwtUtil.removeOldRefreshTokenByUUID(loginDto.uuid) }
            verify { passwordEncoder.matches(loginDto.password, user.password) }
            verify { jwtUtil.generateAccessToken(any()) }
            verify { jwtUtil.generateRefreshToken(any()) }
        }

        @Test
        fun `login should return JwtAuthenticationException when user credentials are incorrect`() {
            every { usersService.getByEmail(user.email) } returns user
            every { jwtUtil.removeOldRefreshTokenByUUID(loginDto.uuid) } returns Unit
            every { passwordEncoder.matches(loginDto.password, user.password) } returns false

            val exception = assertFailsWith<JwtAuthenticationException> {
                serviceUnderTest.login(loginDto)
            }

            assertThat(exception.message).isEqualTo("Wrong email or password")

            verify { usersService.getByEmail(user.email) }
            verify { jwtUtil.removeOldRefreshTokenByUUID(loginDto.uuid) }
            verify { passwordEncoder.matches(loginDto.password, user.password) }

            verify(exactly = 0) { jwtUtil.generateAccessToken(any()) }
            verify(exactly = 0)  { jwtUtil.generateRefreshToken(any()) }
        }

        @Test
        fun `login should return JwtAuthenticationException when user not found`() {
            every { usersService.getByEmail(user.email, eq(false)) } returns null

            val exception = assertFailsWith<JwtAuthenticationException> {
                serviceUnderTest.login(loginDto)
            }

            assertThat(exception.message).isEqualTo("Wrong email or password")

            verify { usersService.getByEmail(user.email, eq(false)) }

            verify(exactly = 0) { jwtUtil.removeOldRefreshTokenByUUID(loginDto.uuid) }
            verify(exactly = 0) { passwordEncoder.matches(loginDto.password, user.password) }
            verify(exactly = 0) { jwtUtil.generateAccessToken(any()) }
            verify(exactly = 0)  { jwtUtil.generateRefreshToken(any()) }
        }
    }
}