package org.careerseekers.userservice.services.authService

import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.careerseekers.userservice.mocks.generators.UsersGenerator.createUser
import org.careerseekers.userservice.dto.auth.UpdateUserTokensDto
import org.careerseekers.userservice.dto.jwt.UserTokensDto
import org.careerseekers.userservice.exceptions.JwtAuthenticationException
import org.careerseekers.userservice.mocks.AuthServiceMocks
import org.junit.jupiter.api.Nested
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertFailsWith

class AuthServiceUpdateTokensTests : AuthServiceMocks() {

    @Nested
    inner class UpdateTokensTests {
        private val user = createUser()
        private val dto = UpdateUserTokensDto(
            accessToken = "dummyAccessToken",
            refreshToken = "dummyRefreshToken",
            uuid = UUID.randomUUID()
        )

        @Test
        fun `updateTokens should return new UserTokensDto`() {
            every { jwtUtil.verifyToken(dto.accessToken, throwTimeLimit = eq(false)) } returns true
            every { jwtUtil.verifyToken(dto.refreshToken, uuid = dto.uuid) } returns true
            every { jwtUtil.removeOldRefreshTokenByUUID(dto.uuid) } returns Unit
            every { jwtUtil.getUserFromToken(eq(dto.refreshToken)) } returns user
            every { jwtUtil.generateAccessToken(any()) } returns "accessToken"
            every { jwtUtil.generateRefreshToken(any()) } returns "refreshToken"

            val result = serviceUnderTest.updateTokens(dto)

            assertThat(result).isEqualTo(
                UserTokensDto(
                    accessToken = "accessToken",
                    refreshToken = "refreshToken"
                )
            )

            verify { jwtUtil.verifyToken(dto.accessToken, throwTimeLimit = eq(false)) }
            verify { jwtUtil.verifyToken(dto.refreshToken, any()) }
            verify { jwtUtil.removeOldRefreshTokenByUUID(dto.uuid) }
            verify { jwtUtil.getUserFromToken(eq(dto.refreshToken)) }
            verify { jwtUtil.generateAccessToken(any()) }
            verify { jwtUtil.generateRefreshToken(any()) }
        }

        @Test
        fun `updateTokens should return JwtAuthenticationException if access token claims broken`() {
            every {
                jwtUtil.verifyToken(
                    dto.accessToken,
                    throwTimeLimit = eq(false)
                )
            } throws JwtAuthenticationException(
                "Invalid token claims"
            )

            val exception = assertFailsWith<JwtAuthenticationException> {
                serviceUnderTest.updateTokens(dto)
            }

            assertThat(exception.message).isEqualTo("Invalid token claims")

            verify { jwtUtil.verifyToken(dto.accessToken, throwTimeLimit = eq(false)) }

            verify(exactly = 0) { jwtUtil.verifyToken(dto.refreshToken, any()) }
            verify(exactly = 0) { jwtUtil.removeOldRefreshTokenByUUID(dto.uuid) }
            verify(exactly = 0) { jwtUtil.getUserFromToken(eq(dto.refreshToken)) }
            verify(exactly = 0) { jwtUtil.generateAccessToken(any()) }
            verify(exactly = 0) { jwtUtil.generateRefreshToken(any()) }
        }

        @Test
        fun `updateTokens should return JwtAuthenticationException if refresh token claims broken`() {
            every { jwtUtil.verifyToken(dto.accessToken, throwTimeLimit = eq(false)) } returns true
            every { jwtUtil.verifyToken(dto.refreshToken, uuid = dto.uuid) } throws JwtAuthenticationException(
                "Invalid token claims"
            )

            val exception = assertFailsWith<JwtAuthenticationException> {
                serviceUnderTest.updateTokens(dto)
            }

            assertThat(exception.message).isEqualTo("Invalid token claims")

            verify { jwtUtil.verifyToken(dto.accessToken, throwTimeLimit = eq(false)) }
            verify { jwtUtil.verifyToken(dto.refreshToken, any()) }

            verify(exactly = 0) { jwtUtil.removeOldRefreshTokenByUUID(dto.uuid) }
            verify(exactly = 0) { jwtUtil.getUserFromToken(eq(dto.refreshToken)) }
            verify(exactly = 0) { jwtUtil.generateAccessToken(any()) }
            verify(exactly = 0) { jwtUtil.generateRefreshToken(any()) }
        }

        @Test
        fun `updateTokens should return JwtAuthenticationException if refresh token not found in storage`() {
            every { jwtUtil.verifyToken(dto.accessToken, throwTimeLimit = eq(false)) } returns true
            every { jwtUtil.verifyToken(dto.refreshToken, uuid = dto.uuid) } throws JwtAuthenticationException(
                "Invalid token metadata! JWT validity cannot be asserted and should not be trusted."
            )

            val exception = assertFailsWith<JwtAuthenticationException> {
                serviceUnderTest.updateTokens(dto)
            }

            assertThat(exception.message).isEqualTo("Invalid token metadata! JWT validity cannot be asserted and should not be trusted.")

            verify { jwtUtil.verifyToken(dto.accessToken, throwTimeLimit = eq(false)) }
            verify { jwtUtil.verifyToken(dto.refreshToken, any()) }

            verify(exactly = 0) { jwtUtil.removeOldRefreshTokenByUUID(dto.uuid) }
            verify(exactly = 0) { jwtUtil.getUserFromToken(eq(dto.refreshToken)) }
            verify(exactly = 0) { jwtUtil.generateAccessToken(any()) }
            verify(exactly = 0) { jwtUtil.generateRefreshToken(any()) }
        }

        @Test
        fun `updateTokens should return NotFoundException id user from token not found`() {
            every { jwtUtil.verifyToken(dto.accessToken, throwTimeLimit = eq(false)) } returns true
            every { jwtUtil.verifyToken(dto.refreshToken, uuid = dto.uuid) } returns true
            every { jwtUtil.removeOldRefreshTokenByUUID(dto.uuid) } returns Unit
            every { jwtUtil.getUserFromToken(eq(dto.refreshToken)) } returns null

            val exception = assertFailsWith<JwtAuthenticationException> {
                serviceUnderTest.updateTokens(dto)
            }

            assertThat(exception.message).isEqualTo("Invalid refresh token")

            verify { jwtUtil.verifyToken(dto.accessToken, throwTimeLimit = eq(false)) }
            verify { jwtUtil.verifyToken(dto.refreshToken, any()) }
            verify { jwtUtil.removeOldRefreshTokenByUUID(dto.uuid) }
            verify { jwtUtil.getUserFromToken(eq(dto.refreshToken)) }

            verify(exactly = 0) { jwtUtil.generateAccessToken(any()) }
            verify(exactly = 0) { jwtUtil.generateRefreshToken(any()) }
        }
    }
}