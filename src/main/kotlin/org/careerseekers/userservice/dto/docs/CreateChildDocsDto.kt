package org.careerseekers.userservice.dto.docs

import org.careerseekers.userservice.dto.DtoClass
import org.springframework.web.multipart.MultipartFile

data class CreateChildDocsDto(
    val childId: Long,
    val snilsNumber: String,
    val snilsFile: MultipartFile?,
    val studyingPlace: String,
    val studyingCertificateFile: MultipartFile,
    val learningClass: Short,
    val trainingGround: String,
    val additionalStudyingCertificateFile: MultipartFile,
    val parentRole: String,
    val consentToChildPdpFile: MultipartFile,
    val birthCertificateFile: MultipartFile,
    var snilsId: Long? = null,
    var studyingCertificateId: Long? = null,
    var additionalStudyingCertificateId: Long? = null,
    var consentToChildPdpId: Long? = null,
    var birthCertificateId: Long? = null,
) : DtoClass
