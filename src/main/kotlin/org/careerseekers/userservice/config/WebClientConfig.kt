package org.careerseekers.userservice.config

import org.careerseekers.userservice.exceptions.ConnectionRefusedException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException

@Configuration
class WebClientConfig {
    @Value("\${file-service.uri}")
    private lateinit var fileServiceUri: String

    @Bean
    @Qualifier("file-service")
    fun fileServiceClient(): WebClient {
        return WebClient.builder()
            .baseUrl(fileServiceUri)
            .filter { request, next ->
                next.exchange(request)
                    .onErrorMap { ex ->
                        if (ex is WebClientRequestException) {
                            ConnectionRefusedException("Connection to file-service with uri $fileServiceUri refused")
                        } else ex
                    }
            }
            .build()
    }
}