package org.careerseekers.userservice.filters

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.careerseekers.userservice.cache.UserAuthAttemptsCacheClient
import org.careerseekers.userservice.dto.UserAuthAttemptsDto
import org.careerseekers.userservice.functions.sendError
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class LoginRateLimitFilter(private val cacheClient: UserAuthAttemptsCacheClient) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (request.requestURI != "/users-service/v1/auth/login") {
            filterChain.doFilter(request, response)
            return
        }

        val email = request.getHeader("User-Email") ?: run {
            sendError(response, 400, "User-Email is missing.")
            return
        }

        val attempts = cacheClient.getItemFromCache(email)?.attempt ?: 0
        if (attempts >= 5) {
            sendError(response, 429, "Превышено количество попыток входа. Попробуйте позже.")
            return
        }

        cacheClient.loadItemToCache(UserAuthAttemptsDto(email, attempts + 1))

        filterChain.doFilter(request, response)
    }
}