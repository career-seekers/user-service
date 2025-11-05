package org.careerseekers.userservice.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.careerseekers.userservice.exceptions.JwtAuthenticationException
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import kotlin.text.startsWith
import kotlin.text.substring

@Component
class JwtAuthenticationEntryPoint(private val jwtUtil: JwtUtil) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authException: AuthenticationException?
    ) {
        response?.contentType = "application/json"
        val auth = request?.getHeader("Authorization")

        if (auth != null && auth.startsWith("Bearer ")) {
            val jwtToken = auth.substring(7)

            try {
                jwtUtil.verifyToken(jwtToken)
            } catch (e: JwtAuthenticationException) {
                var body: String?

                if (e.message!!.contains("Невалидное содержание токена.") || e.message!!.contains("Invalid token!")) {
                    response?.status = HttpServletResponse.SC_UNAUTHORIZED
                    body = """{"status": ${HttpStatus.UNAUTHORIZED.value()}, "message": ${e.message}}"""
                } else {
                    response?.status = HttpServletResponse.SC_FORBIDDEN
                    body = """{"status": ${HttpStatus.FORBIDDEN.value()}, "message": "Срок жизни токена истёк."}"""
                }

                response?.writer?.write(body)
            }
        } else {
            response?.status = HttpServletResponse.SC_UNAUTHORIZED
            val body =
                """{"status": ${HttpStatus.UNAUTHORIZED.value()}, "message": "You'll be authorized to access this resource"}"""

            response?.writer?.write(body)
        }
    }
}