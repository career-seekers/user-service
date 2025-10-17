package org.careerseekers.userservice.controllers

import org.careerseekers.userservice.annotations.AccessUntil
import org.careerseekers.userservice.dto.docs.CreateChildDocsDto
import org.careerseekers.userservice.dto.docs.UpdateChildDocsDto
import org.careerseekers.userservice.entities.ChildDocuments
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.careerseekers.userservice.io.converters.extensions.toHttpResponse
import org.careerseekers.userservice.io.converters.extensions.toLongOrThrow
import org.careerseekers.userservice.io.converters.extensions.toShortOrThrow
import org.careerseekers.userservice.services.ChildDocsFixService
import org.careerseekers.userservice.services.ChildDocumentsService
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
@RequestMapping("/users-service/v1/child-docs")
class ChildDocsController(
    private val service: ChildDocumentsService,
    private val childDocsFixService: ChildDocsFixService
) {

    @GetMapping("/")
    fun getAll() = service.getAll().toHttpResponse()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) =
        service.getById(id, throwable = true, message = "Документы ребенка с ID $id не найдены")!!.toHttpResponse()

    @GetMapping("/getByChildId/{id}")
    fun getByChildId(@PathVariable id: Long) = service.getByChildId(id)!!.toHttpResponse()

    @GetMapping("/getBySnilsNumber/{snilsNumber}")
    fun getBySnilsNumber(@PathVariable snilsNumber: String) = service.getBySnilsNumber(snilsNumber)!!.toHttpResponse()

    @AccessUntil(
        until = "2025-10-15T23:59:59+03:00",
        errorMessage = "Срок изменения данных о ребенке закончился 15.10.2025 в 23:59.",
        allowedRoles = [UsersRoles.ADMIN]
    )
    @PostMapping("/")
    fun create(
        @RequestPart("childId") childId: String,
        @RequestPart("snilsNumber") snilsNumber: String,
        @RequestPart("snilsFile") snilsFile: MultipartFile,
        @RequestPart("studyingPlace") studyingPlace: String,
        @RequestPart("studyingCertificateFile") studyingCertificateFile: MultipartFile,
        @RequestPart("learningClass") learningClass: String,
        @RequestPart("trainingGround") trainingGround: String,
        @RequestPart("additionalStudyingCertificateFile") additionalStudyingCertificateFile: MultipartFile,
        @RequestPart("parentRole") parentRole: String,
        @RequestPart("consentToChildPdpFile") consentToChildPdpFile: MultipartFile,
        @RequestPart("birthCertificateFile") birthCertificateFile: MultipartFile,
    ): BasicSuccessfulResponse<ChildDocuments> {
        val dto = CreateChildDocsDto(
            childId = childId.toLongOrThrow(),
            snilsNumber = snilsNumber,
            snilsFile = snilsFile,
            studyingPlace = studyingPlace,
            studyingCertificateFile = studyingCertificateFile,
            learningClass = learningClass.toShortOrThrow(),
            trainingGround = trainingGround,
            additionalStudyingCertificateFile = additionalStudyingCertificateFile,
            parentRole = parentRole,
            consentToChildPdpFile = consentToChildPdpFile,
            birthCertificateFile = birthCertificateFile,
        )

        return service.create(dto).toHttpResponse()
    }

    @AccessUntil(
        until = "2025-10-15T23:59:59+03:00",
        errorMessage = "Срок изменения данных о ребенке закончился 15.10.2025 в 23:59.",
        allowedRoles = [UsersRoles.ADMIN]
    )
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
        @RequestPart("birthCertificateFile", required = false) birthCertificate: MultipartFile?,
    ): BasicSuccessfulResponse<String> {
        val dto = UpdateChildDocsDto(
            id = id.toLongOrThrow(),
            snilsNumber = snilsNumber,
            snilsFile = snilsFile,
            studyingPlace = studyingPlace,
            studyingCertificateFile = studyingCertificateFile,
            learningClass = learningClass?.toShortOrThrow(),
            trainingGround = trainingGround,
            additionalStudyingCertificateFile = additionalStudyingCertificateFile,
            parentRole = parentRole,
            consentToChildPdpFile = consentToChildPdpFile,
            birthCertificate = birthCertificate,
        )

        return service.update(dto).toHttpResponse()
    }

    @PatchMapping("/fixDocs")
    fun fixDocs() = childDocsFixService.fixChildDocs()

    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable id: Long) = service.deleteById(id).toHttpResponse()

    @DeleteMapping("/")
    fun deleteAll() = service.deleteAll().toHttpResponse()
}