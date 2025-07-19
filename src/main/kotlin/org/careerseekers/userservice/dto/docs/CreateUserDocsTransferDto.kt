package org.careerseekers.userservice.dto.docs

import org.careerseekers.userservice.dto.DtoClass

data class CreateUserDocsTransferDto(
    val userId: Long,
    var snilsNumber: String,
    var snilsId: Long?,
    var studyingPlace: String,
    var studyingCertificateId: Long?,
    var learningClass: Short,
    var trainingGround: String,
    var additionalStudyingCertificateId: Long?,
    var parentRole: String,
    var consentToChildPdpId: Long?,
) : DtoClass
