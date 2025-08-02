package org.careerseekers.userservice.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.careerseekers.userservice.dto.files.FileStructure
import org.careerseekers.userservice.exceptions.BadRequestException
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.io.BasicErrorResponse
import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Utility
class DocumentsApiResolver(
    @Qualifier("file-service") private val httpClient: WebClient
) {
    private val documentCleanupScope = CoroutineScope(Dispatchers.IO)

    private fun deleteDocumentAsync(id: Long) =
        documentCleanupScope.launch { deleteDocument(id) }

    fun loadDocId(url: String, file: MultipartFile?): Long? =
        file?.let {
            val id = loadDocument(url, it)?.id
            id?.let { registerFileForRollback(it) }
            id
        }

    private fun registerFileForRollback(docId: Long) {
        TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
            override fun afterCompletion(status: Int) {
                if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
                    deleteDocumentAsync(docId)
                }
            }
        })
    }

    private fun loadDocument(url: String, file: MultipartFile): FileStructure? {
        val resource = object : ByteArrayResource(file.bytes) {
            override fun getFilename(): String? = file.originalFilename
        }

        val multipartData = LinkedMultiValueMap<String, Any>()
        multipartData.add("file", resource)

        return httpClient.post()
            .uri("/file-service/v1/files/$url")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(multipartData))
            .retrieve()
            .bodyToMono(FileStructure::class.java)
            .block()
    }

    fun deleteDocument(id: Long, throwable: Boolean = true): BasicSuccessfulResponse<*>? {
        return httpClient.delete()
            .uri("/file-service/v1/files/$id")
            .retrieve()
            .onStatus({ it.isError }) { resp ->
                resp.bodyToMono(String::class.java).flatMap { body ->
                    val json = Json { ignoreUnknownKeys = true }
                    val apiError = json.decodeFromString<BasicErrorResponse>(body)
                    if (throwable) {
                        if (apiError.status == 404) {
                            Mono.error(NotFoundException(apiError.message))
                        } else {
                            Mono.error(BadRequestException(apiError.message + " " + apiError.status))
                        }
                    } else {
                        Mono.empty()
                    }
                }
            }
            .bodyToMono(BasicSuccessfulResponse::class.java)
            .block()
    }

}