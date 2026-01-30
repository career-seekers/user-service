package org.careerseekers.userservice.filters

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.careerseekers.userservice.cache.UserAuthAttemptsCacheClient
import org.careerseekers.userservice.dto.UserAuthAttemptsDto
import org.careerseekers.userservice.exceptions.BadRequestException
import org.springframework.http.HttpStatus
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

        try {
            val email = request.getHeader("User-Email") ?: throw BadRequestException("User-Email is missing.")

            cacheClient.getItemFromCache(email)?.let { cacheItem ->
                if (cacheItem.attempt > 5) {
                    response.status = HttpStatus.TOO_MANY_REQUESTS.value()
                    response.contentType = "application/json"
                    response.characterEncoding = "UTF-8"
                    response.writer.write("""{"status":429,"message":"Превышено количество попыток входа. Попробуйте позже."}""")
                    return
                }
            } ?: run {
                cacheClient.loadItemToCache(UserAuthAttemptsDto(email, 0))
                filterChain.doFilter(request, response)
            }

            filterChain.doFilter(request, response)
        } catch (e: BadRequestException) {
            response.status = HttpStatus.BAD_REQUEST.value()
            response.contentType = "application/json"
            response.characterEncoding = "UTF-8"
            response.writer.write("""{"status":400,"message":"${e.message}"}""")
            return
        }
    }
}