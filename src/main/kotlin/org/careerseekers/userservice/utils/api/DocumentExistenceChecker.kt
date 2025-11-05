package org.careerseekers.userservice.utils.api

import kotlinx.serialization.json.Json
import org.careerseekers.userservice.annotations.Utility
import org.careerseekers.userservice.dto.files.FileStructure
import org.careerseekers.userservice.enums.FileTypes
import org.careerseekers.userservice.exceptions.BadRequestException
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.io.BasicErrorResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Utility
class DocumentExistenceChecker(
    @param:Qualifier("file-service") private val httpClient: WebClient
) {
    fun checkFileExistence(fileId: Long, fileType: FileTypes?): FileStructure? {
        return httpClient.get()
            .uri("/file-service/v1/files/$fileId")
            .retrieve()
            .onStatus({ it.isError }) { resp ->
                resp.bodyToMono(String::class.java).flatMap { body ->
                    val json = Json { ignoreUnknownKeys = true }
                    val apiError = json.decodeFromString<BasicErrorResponse>(body)

                    if (apiError.status == 404) {
                        Mono.error(NotFoundException("File not found"))
                    } else {
                        Mono.error(BadRequestException(apiError.message))
                    }
                }
            }
            .bodyToMono(FileStructure::class.java)
            .filter { file -> fileType == null || file.fileType == fileType }
            .switchIfEmpty(Mono.error(BadRequestException("The specified and current file types don't match")))
            .block()
    }
}