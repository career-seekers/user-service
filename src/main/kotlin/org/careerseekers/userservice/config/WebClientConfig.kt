package org.careerseekers.userservice.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {
    @Value("\${file-service.uri}")
    private lateinit var fileServiceUri: String

    @Bean
    @Qualifier("file-service")
    fun fileServiceClient(): WebClient {
        return WebClient.builder()
            .baseUrl(fileServiceUri)
            .build()
    }
}