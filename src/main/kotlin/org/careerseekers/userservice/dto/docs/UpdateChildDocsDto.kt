package org.careerseekers.userservice.dto.docs

import org.careerseekers.userservice.dto.DtoClass
import org.springframework.web.multipart.MultipartFile

data class UpdateChildDocsDto(
    val id: Long,
    val snilsNumber: String?,
    val snilsFile: MultipartFile?,
    val studyingPlace: String?,
    val studyingCertificateFile: MultipartFile?,
    val learningClass: Short?,
    val trainingGround: String?,
    val additionalStudyingCertificateFile: MultipartFile?,
    val parentRole: String?,
    val consentToChildPdpFile: MultipartFile?,
    val birthCertificate: MultipartFile?
) : DtoClass
