package org.careerseekers.userservice.filters

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.careerseekers.userservice.security.JwtUtil
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.naming.AuthenticationException
import kotlin.text.startsWith
import kotlin.text.substring

@Component
class JwtRequestFilter(
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val authorizationHeader = request.getHeader("Authorization")

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                val jwtToken = authorizationHeader.substring(7)

                if (jwtUtil.verifyToken(jwtToken)) {
                    val user = jwtUtil.getUserFromToken(jwtToken)
                    val authorities = listOf(SimpleGrantedAuthority(user?.role.toString()))

                    if (SecurityContextHolder.getContext().authentication == null) {
                        val authReq = UsernamePasswordAuthenticationToken(user?.email, null, authorities)
                        SecurityContextHolder.getContext().authentication = authReq
                    }
                }
            }

            filterChain.doFilter(request, response)
        } catch (ex: AuthenticationException) {
            SecurityContextHolder.clearContext()
            throw ex
        }
    }
}