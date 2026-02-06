package org.careerseekers.userservice.interceptors

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.careerseekers.userservice.dto.UserLoginMessageDto
import org.careerseekers.userservice.services.kafka.producers.UserLoginKafkaProducer
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class LoginStatsInterceptor(private val userLoginKafkaProducer: UserLoginKafkaProducer) : HandlerInterceptor {

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        if (response.status == HttpServletResponse.SC_OK) {
            val email = request.getHeader("User-Email") ?: return
            userLoginKafkaProducer.sendMessage(UserLoginMessageDto(
                email = email
            ))
        }
    }
}