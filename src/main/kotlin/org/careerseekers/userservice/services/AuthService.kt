package org.careerseekers.userservice.services

import org.careerseekers.userservice.cache.TemporaryPasswordsCache
import org.careerseekers.userservice.dto.TemporaryPasswordDto
import org.careerseekers.userservice.dto.auth.LoginUserDto
import org.careerseekers.userservice.dto.auth.RegisterUserDto
import org.careerseekers.userservice.dto.auth.UpdateUserTokensDto
import org.careerseekers.userservice.dto.jwt.CreateJwtToken
import org.careerseekers.userservice.dto.jwt.UserTokensDto
import org.careerseekers.userservice.dto.users.CreateUserDto
import org.careerseekers.userservice.exceptions.JwtAuthenticationException
import org.careerseekers.userservice.utils.JwtUtil
import org.careerseekers.userservice.utils.PasswordGenerator.generatePassword
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val jwtUtil: JwtUtil,
    private val usersService: UsersService,
    private val passwordEncoder: PasswordEncoder,
    private val temporaryPasswordsCache: TemporaryPasswordsCache,
) {
    @Transactional
    fun register(data: RegisterUserDto): UserTokensDto {
        return usersService.create(CreateUserDto(
            firstName = data.firstName,
            lastName = data.lastName,
            patronymic = data.patronymic,
            dateOfBirth = data.dateOfBirth,
            email = data.email,
            mobileNumber = data.mobileNumber,
            password = data.password ?: generatePassword(data.email),
            role = data.role,
            avatarId = data.avatarId,
        )).let {
            jwtUtil.removeOldRefreshTokenByUUID(data.uuid)
            UserTokensDto(
                jwtUtil.generateAccessToken(CreateJwtToken(it, data.uuid)),
                jwtUtil.generateRefreshToken(CreateJwtToken(it, data.uuid))
            )
        }
    }

    @Transactional
    fun login(data: LoginUserDto): UserTokensDto {
        return usersService.getByEmail(data.email).let {
            jwtUtil.removeOldRefreshTokenByUUID(data.uuid)
            if (passwordEncoder.matches(data.password, it!!.password)) {
                UserTokensDto(
                    jwtUtil.generateAccessToken(CreateJwtToken(it, data.uuid)),
                    jwtUtil.generateRefreshToken(CreateJwtToken(it, data.uuid))
                )
            } else throw JwtAuthenticationException("Wrong email or password")
        }
    }

    @Transactional
    fun updateTokens(data: UpdateUserTokensDto): UserTokensDto {
        jwtUtil.verifyToken(data.accessToken, throwTimeLimit = false)
        jwtUtil.verifyToken(data.refreshToken, data.uuid)
        jwtUtil.removeOldRefreshTokenByUUID(data.uuid)

        return jwtUtil.getUserFromToken(data.refreshToken)?.run {
            UserTokensDto(
                jwtUtil.generateAccessToken(CreateJwtToken(this, data.uuid)),
                jwtUtil.generateRefreshToken(CreateJwtToken(this, data.uuid))
            )
        } ?: throw JwtAuthenticationException("Invalid refresh token")
    }

    private fun generatePassword(email: String): String {
        val password = generatePassword()
        temporaryPasswordsCache.loadItemToCache(TemporaryPasswordDto(email, password))

        return passwordEncoder.encode(password)
    }
}