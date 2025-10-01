package org.careerseekers.userservice.controllers

import org.careerseekers.userservice.dto.users.CreateChildPackDto
import org.careerseekers.userservice.entities.Children
import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.careerseekers.userservice.io.converters.extensions.toLongOrThrow
import org.careerseekers.userservice.io.converters.extensions.toShortOrThrow
import org.careerseekers.userservice.services.ChildPackService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RestController
@RequestMapping("/users-service/v1/children")
class ChildPackController(private val childPackService: ChildPackService) {

    @PostMapping("/createChildPack")
    fun createChildPack(
        @RequestPart("lastName") lastName: String,
        @RequestPart("firstName") firstName: String,
        @RequestPart("patronymic") patronymic: String,
        @RequestPart("dateOfBirth") dateOfBirth: String,
        @RequestPart("userId") userId: String,
        @RequestPart("mentorId") mentorId: String,
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
    ): BasicSuccessfulResponse<Children> {
        val body = CreateChildPackDto(
            lastName = lastName,
            firstName = firstName,
            patronymic = patronymic,
            dateOfBirth = stringToDate(dateOfBirth)!!,
            userId = userId.toLongOrThrow(),
            mentorId = mentorId.toLongOrThrow(),
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

        return childPackService.createChildPack(body).toHttpResponse()
    }

    fun stringToDate(dateString: String, format: String = "yyyy-MM-dd"): Date? {
        return try {
            val formatter = SimpleDateFormat(format, Locale.getDefault())
            formatter.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}