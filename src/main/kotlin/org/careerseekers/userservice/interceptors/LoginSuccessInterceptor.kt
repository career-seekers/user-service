package org.careerseekers.userservice.interceptors

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.careerseekers.userservice.cache.UserAuthAttemptsCacheClient
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.lang.Exception

@Component
class LoginSuccessInterceptor(private val cacheClient: UserAuthAttemptsCacheClient) : HandlerInterceptor {

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        if (response.status == HttpServletResponse.SC_OK) {
            val email = request.getHeader("User-Email") ?: return
            cacheClient.deleteCacheItem(email)
        }
    }
}