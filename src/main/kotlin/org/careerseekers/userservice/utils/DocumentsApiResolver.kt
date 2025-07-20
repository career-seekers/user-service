package org.careerseekers.userservice.utils

import kotlinx.serialization.json.Json
import org.careerseekers.userservice.dto.files.FileStructure
import org.careerseekers.userservice.exceptions.BadRequestException
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.io.BasicErrorResponse
import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class DocumentsApiResolver(
    @Qualifier("file-service") private val httpClient: WebClient
) {
    fun loadDocument(url: String, file: MultipartFile): FileStructure? {
        return httpClient.post()
            .uri("/file-service/v1/files/$url")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData("file", file))
            .retrieve()
            .bodyToMono(FileStructure::class.java)
            .block()
    }

    @Transactional
    fun deleteDocument(id: Long) {
        httpClient.delete()
            .uri("/file-service/v1/files/$id")
            .retrieve()
            .onStatus({ it.isError }) { resp ->
                resp.bodyToMono(String::class.java).flatMap { body ->
                    val json = Json { ignoreUnknownKeys = true }
                    val apiError = json.decodeFromString<BasicErrorResponse>(body)

                    if (apiError.status == 404) {
                        Mono.error(NotFoundException(apiError.message))
                    } else {
                        Mono.error(BadRequestException(apiError.message))
                    }
                }
            }
            .bodyToMono(BasicSuccessfulResponse::class.java)
            .block()
    }
}