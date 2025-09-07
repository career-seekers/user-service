package org.careerseekers.userservice.dto.docs

import org.careerseekers.userservice.dto.DtoClass
import org.careerseekers.userservice.entities.Users

data class CreateUserDocsTransferDto(
    val user: Users,
    var snilsNumber: String,
    var snilsId: Long?,
    var studyingPlace: String,
    var studyingCertificateId: Long?,
    var learningClass: Short,
    var trainingGround: String,
    var additionalStudyingCertificateId: Long?,
    var parentRole: String,
    var consentToChildPdpId: Long?,
    var birthCertificateId: Long?,
) : DtoClass
