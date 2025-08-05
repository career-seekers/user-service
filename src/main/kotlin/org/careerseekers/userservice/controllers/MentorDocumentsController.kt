package org.careerseekers.userservice.controllers

import org.careerseekers.userservice.controllers.interfaces.crud.IDeleteController
import org.careerseekers.userservice.controllers.interfaces.crud.IReadController
import org.careerseekers.userservice.dto.docs.CreateMentorDocsDto
import org.careerseekers.userservice.dto.docs.UpdateMentorDocsDto
import org.careerseekers.userservice.entities.MentorDocuments
import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.careerseekers.userservice.io.converters.extensions.toHttpResponse
import org.careerseekers.userservice.io.converters.extensions.toLongOrThrow
import org.careerseekers.userservice.services.MentorDocumentsService
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
@RequestMapping("users-service/v1/mentor-docs")
class MentorDocumentsController(
    override val service: MentorDocumentsService
) : IReadController<MentorDocuments, Long>,
    IDeleteController<MentorDocuments, Long> {
    @GetMapping("/{id}")
    override fun getById(@PathVariable id: Long) = service.getById(id)

    @GetMapping("/")
    override fun getAll() = service.getAll()

    @GetMapping("getByUserId/{id}")
    fun getDocsByUserId(@PathVariable id: Long) = service.getDocsByUserId(id)

    @PostMapping("/")
    fun create(
        @RequestPart("userId") userId: String,
        @RequestPart("institution") institution: String,
        @RequestPart("post") post: String,
        @RequestPart("consentToMentorPdp") consentToMentorPdp: MultipartFile,
    ): BasicSuccessfulResponse<MentorDocuments> {
        val body = CreateMentorDocsDto(
            userId = userId.toLongOrThrow(),
            institution = institution,
            post = post,
            consentToMentorPdp = consentToMentorPdp,
        )

        return service.create(body).toHttpResponse()
    }

    @PatchMapping("/")
    fun update(
        @RequestPart("id") id: String,
        @RequestPart("institution", required = false) institution: String?,
        @RequestPart("post", required = false) post: String?,
        @RequestPart("consentToMentorPdp", required = false) consentToMentorPdp: MultipartFile,
    ): BasicSuccessfulResponse<String> {
        val body = UpdateMentorDocsDto(
            id = id.toLongOrThrow(),
            institution = institution,
            post = post,
            consentToMentorPdp = consentToMentorPdp
        )

        return service.update(body).toHttpResponse()
    }

    @DeleteMapping("/{id}")
    override fun deleteById(@PathVariable id: Long): BasicSuccessfulResponse<String> {
        return service.deleteById(id).toHttpResponse()
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/")
    override fun deleteAll() = service.deleteAll().toHttpResponse()
}