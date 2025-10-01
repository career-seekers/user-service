package org.careerseekers.userservice.dto.users

import org.careerseekers.userservice.dto.DtoClass
import org.careerseekers.userservice.entities.Children
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.enums.DirectionAgeCategory
import org.springframework.web.multipart.MultipartFile
import java.util.Date

data class CreateChildPackDto(
    val lastName: String,
    val firstName: String,
    val patronymic: String,
    val dateOfBirth: Date,
    val createdAt: Date = Date(),
    val userId: Long,
    val mentorId: Long? = null,
    var user: Users? = null,
    var mentor: Users? = null,

    var child: Children? = null,
    val snilsNumber: String,
    val snilsFile: MultipartFile,
    val studyingPlace: String,
    val studyingCertificateFile: MultipartFile,
    val learningClass: Short,
    var ageCategory: DirectionAgeCategory? = null,
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
