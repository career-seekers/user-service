package org.careerseekers.userservice.utils

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Jwts.SIG
import io.jsonwebtoken.security.Keys
import org.careerseekers.userservice.annotations.Utility
import org.careerseekers.userservice.config.JwtProperties
import org.careerseekers.userservice.dto.jwt.CreateJwtToken
import org.careerseekers.userservice.dto.jwt.SaveRefreshTokenDto
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.exceptions.JwtAuthenticationException
import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.careerseekers.userservice.mappers.JwtTokensStorageMapper
import org.careerseekers.userservice.repositories.JwtTokensRepository
import org.careerseekers.userservice.services.UsersService
import org.springframework.context.annotation.Lazy
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.Date
import java.util.UUID

@Utility
class JwtUtil(
    private val jwtProperties: JwtProperties,
    private val jwtTokensRepository: JwtTokensRepository,
    private val jwtTokensStorageMapper: JwtTokensStorageMapper,
    @param:Lazy private val usersService: UsersService,
) {
    fun generateAccessToken(data: CreateJwtToken): String = generateToken(data, jwtProperties.accessTokenExpiration)

    @Transactional
    fun generateRefreshToken(data: CreateJwtToken): String {
        val token = generateToken(data, jwtProperties.refreshTokenExpiration, false)
        saveRefreshToken(data, token)

        return token
    }

    private fun generateToken(data: CreateJwtToken, expiration: String, accessToken: Boolean = true): String {
        val claims: MutableMap<String, Any> = mutableMapOf()
        claims["id"] = data.user.id
        claims["uuid"] = data.uuid
        if (accessToken) {
            claims["email"] = data.user.email
            claims["role"] = data.user.role
        }

        return Jwts.builder()
            .claims(claims)
            .issuedAt(Date())
            .expiration(Date.from(Instant.now().plusSeconds(expiration.toLong())))
            .signWith(Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray()), SIG.HS512)
            .compact()
    }

    fun saveRefreshToken(data: CreateJwtToken, token: String): BasicSuccessfulResponse<String> {
        val o = SaveRefreshTokenDto(data.user, data.uuid, token)
        jwtTokensRepository.save(jwtTokensStorageMapper.tokenFromSaveRefreshDto(o))

        return BasicSuccessfulResponse("Токен сохранён успешно.")
    }

    fun verifyToken(token: String, uuid: UUID? = null, throwTimeLimit: Boolean = true): Boolean {
        val claims = getClaims(token) ?: throw JwtAuthenticationException("Невалидное содержание токена.")
        if (!claims.expiration.after(Date()) && throwTimeLimit) {
            throw JwtAuthenticationException("Срок жизни токена истёк.")
        }

        if (uuid != null && (claims["uuid"].toString() != uuid.toString())) {
            jwtTokensRepository.findByUuid(uuid).let {
                if (it == null) throw JwtAuthenticationException("Недопустимые метаданные токена! Достоверность JWT не может быть подтверждена, и ей не следует доверять.")
            }

            jwtTokensRepository.deleteByToken(token)
            throw JwtAuthenticationException("Недопустимые метаданные токена! Достоверность JWT не может быть подтверждена, и ей не следует доверять.")
        }

        return true
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun removeOldRefreshTokenByUUID(uuid: UUID) = jwtTokensRepository.deleteByUuid(uuid)

    fun getUserFromToken(token: String): Users? {
        val claims = getClaims(token)
        val user = usersService.getById((claims?.get("id") as Int).toLong(), throwable = false)

        return user
    }

    fun getRoleFromToken(token: String): UsersRoles = getClaims(token)?.get("role") as UsersRoles

    fun getClaims(token: String): Claims? {
        val claims = try {
            Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray()))
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (_: ExpiredJwtException) {
            throw JwtAuthenticationException("Срок жизни токена истёк.")
        }

        return claims
    }

}