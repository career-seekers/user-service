package org.careerseekers.userservice.controllers

import org.careerseekers.userservice.dto.docs.CreateExpertDocsDto
import org.careerseekers.userservice.dto.docs.UpdateExpertDocsDto
import org.careerseekers.userservice.entities.ExpertDocuments
import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.careerseekers.userservice.io.converters.extensions.toHttpResponse
import org.careerseekers.userservice.io.converters.extensions.toLongOrThrow
import org.careerseekers.userservice.services.ExpertDocumentsService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/users-service/v1/expert-docs")
class ExpertDocumentsController(
    private val service: ExpertDocumentsService
) {
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) = service.getById(id)

    @GetMapping("/")
    fun getAll() = service.getAll()

    @GetMapping("getByUserId/{id}")
    fun getDocsByUserId(@PathVariable id: Long) = service.getDocsByUserId(id)

    @PostMapping("/")
    fun create(
        @RequestPart("userId") userId: String,
        @RequestPart("institution") institution: String,
        @RequestPart("post") post: String,
        @RequestPart("consentToExpertPdp") consentToExpertPdp: MultipartFile,
    ): BasicSuccessfulResponse<ExpertDocuments> {
        val body = CreateExpertDocsDto(
            userId = userId.toLongOrThrow(),
            institution = institution,
            post = post,
            consentToExpertPdp = consentToExpertPdp,
        )

        return service.create(body).toHttpResponse()
    }

    @PatchMapping("/")
    fun update(
        @RequestPart("id") id: String,
        @RequestPart("institution", required = false) institution: String?,
        @RequestPart("post", required = false) post: String?,
        @RequestPart("consentToExpertPdp", required = false) consentToExpertPdp: MultipartFile,
    ): BasicSuccessfulResponse<String> {
        val body = UpdateExpertDocsDto(
            id = id.toLongOrThrow(),
            institution = institution,
            post = post,
            consentToExpertPdp = consentToExpertPdp
        )

        return service.update(body).toHttpResponse()
    }

    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable id: Long): BasicSuccessfulResponse<String> {
        val res = service.deleteById(id).toHttpResponse()
        return res
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/")
    fun deleteAll() = service.deleteAll().toHttpResponse()
}