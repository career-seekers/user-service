package org.careerseekers.userservice.config

import org.careerseekers.userservice.interceptors.LoginStatsInterceptor
import org.careerseekers.userservice.interceptors.LoginSuccessInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val loginStatsInterceptor: LoginStatsInterceptor,
    private val loginSuccessInterceptor: LoginSuccessInterceptor,
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(loginStatsInterceptor).addPathPatterns("/users-service/v1/auth/login")
        registry.addInterceptor(loginSuccessInterceptor).addPathPatterns("/users-service/v1/auth/login")
    }
}