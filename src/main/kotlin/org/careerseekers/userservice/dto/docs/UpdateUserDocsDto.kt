package org.careerseekers.userservice.dto.docs

import org.careerseekers.userservice.dto.DtoClass
import org.springframework.http.codec.multipart.FilePart

data class UpdateUserDocsDto(
    val id: Long,
    val snilsDto: SnilsDto?,
    val studyingPlace: String?,
    val studyingCertificateFile: FilePart?,
    val learningClass: Short?,
    val trainingGround: String?,
    val additionalStudyingCertificateFile: FilePart?,
    val parentRole: String?,
    val consentToChildPdpFile: FilePart?,
) : DtoClass
