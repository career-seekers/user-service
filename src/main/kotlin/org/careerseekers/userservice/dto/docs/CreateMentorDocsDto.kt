package org.careerseekers.userservice.dto.docs

import org.careerseekers.userservice.dto.DtoClass
import org.springframework.web.multipart.MultipartFile

data class CreateMentorDocsDto(
    val userId: Long,
    val institutions: String,
    val post: String,
    val consentToMentorPdp: MultipartFile,
) : DtoClass
