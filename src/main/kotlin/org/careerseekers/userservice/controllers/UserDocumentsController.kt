package org.careerseekers.userservice.controllers

import org.careerseekers.userservice.controllers.interfaces.crud.IDeleteController
import org.careerseekers.userservice.controllers.interfaces.crud.IReadController
import org.careerseekers.userservice.dto.docs.CreateUserDocsDto
import org.careerseekers.userservice.dto.docs.SnilsDto
import org.careerseekers.userservice.dto.docs.UpdateUserDocsDto
import org.careerseekers.userservice.entities.UserDocuments
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.careerseekers.userservice.io.converters.extensions.toHttpResponse
import org.careerseekers.userservice.io.converters.extensions.toLongOrThrow
import org.careerseekers.userservice.io.converters.extensions.toShortOrThrow
import org.careerseekers.userservice.repositories.UserDocsRepository
import org.careerseekers.userservice.services.UserDocumentsService
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
@RequestMapping("users-service/v1/user-docs")
class UserDocumentsController(
    val repository: UserDocsRepository,
    override val service: UserDocumentsService
) : IReadController<UserDocuments, Long>,
    IDeleteController<UserDocuments, Long> {

    @GetMapping("/")
    override fun getAll() = service.getAll().toHttpResponse()

    @GetMapping("/{id}")
    override fun getById(@PathVariable id: Long) =
        service.getById(id)!!.toHttpResponse()

    @GetMapping("/getByUserId/{userId}")
    fun getByUserId(@PathVariable userId: Long) = service.getDocsByUserId(userId)!!.toHttpResponse()

    @GetMapping("/getBySnilsNumber/{snils}")
    fun getBySnilsNumber(@PathVariable snils: String) = repository.findBySnilsNumber(snils)?.toHttpResponse()
        ?: throw NotFoundException("Documents with snils number $snils not found")

    @PostMapping("/")
    fun create(
        @RequestPart("userId") userId: String,
        @RequestPart("snilsNumber") snilsNumber: String,
        @RequestPart("snilsFile") snilsFile: MultipartFile,
        @RequestPart("studyingPlace") studyingPlace: String,
        @RequestPart("studyingCertificateFile") studyingCertificateFile: MultipartFile,
        @RequestPart("learningClass") learningClass: String,
        @RequestPart("trainingGround") trainingGround: String,
        @RequestPart("additionalStudyingCertificateFile") additionalStudyingCertificateFile: MultipartFile,
        @RequestPart("parentRole") parentRole: String,
        @RequestPart("consentToChildPdpFile") consentToChildPdpFile: MultipartFile,
    ): BasicSuccessfulResponse<UserDocuments> {
        val body = CreateUserDocsDto(
            userId = userId.toLongOrThrow(),
            snilsDto = SnilsDto(
                snilsNumber = snilsNumber,
                snilsFile = snilsFile
            ),
            studyingPlace = studyingPlace,
            studyingCertificateFile = studyingCertificateFile,
            learningClass = learningClass.toShortOrThrow(),
            trainingGround = trainingGround,
            additionalStudyingCertificateFile = additionalStudyingCertificateFile,
            parentRole = parentRole,
            consentToChildPdpFile = consentToChildPdpFile
        )

        return service.create(body).toHttpResponse()
    }

    @PatchMapping("/")
    fun update(
        @RequestPart("id") id: String,
        @RequestPart("snilsNumber", required = false) snilsNumber: String?,
        @RequestPart("snilsFile", required = false) snilsFile: MultipartFile?,
        @RequestPart("studyingPlace", required = false) studyingPlace: String?,
        @RequestPart("studyingCertificateFile", required = false) studyingCertificateFile: MultipartFile?,
        @RequestPart("learningClass", required = false) learningClass: String?,
        @RequestPart("trainingGround", required = false) trainingGround: String?,
        @RequestPart(
            "additionalStudyingCertificateFile",
            required = false
        ) additionalStudyingCertificateFile: MultipartFile?,
        @RequestPart("parentRole", required = false) parentRole: String?,
        @RequestPart("consentToChildPdpFile", required = false) consentToChildPdpFile: MultipartFile?,
    ): BasicSuccessfulResponse<String> {
        val body = UpdateUserDocsDto(
            id = id.toLongOrThrow(),
            snilsNumber = snilsNumber,
            snilsFile = snilsFile,
            studyingPlace = studyingPlace,
            studyingCertificateFile = studyingCertificateFile,
            learningClass = learningClass?.toShortOrThrow(),
            trainingGround = trainingGround,
            additionalStudyingCertificateFile = additionalStudyingCertificateFile,
            parentRole = parentRole,
            consentToChildPdpFile = consentToChildPdpFile
        )

        return service.update(body).toHttpResponse()
    }

    @DeleteMapping("/{id}")
    override fun deleteById(@PathVariable id: Long) = service.deleteById(id).toHttpResponse()

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/")
    override fun deleteAll() = service.deleteAll().toHttpResponse()
}