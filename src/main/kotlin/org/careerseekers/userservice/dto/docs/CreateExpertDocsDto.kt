package org.careerseekers.userservice.dto.docs

import org.careerseekers.userservice.dto.DtoClass
import org.springframework.web.multipart.MultipartFile

data class CreateExpertDocsDto(
    val userId: Long,
    val institution: String,
    val post: String,
    val consentToExpertPdp: MultipartFile,
) : DtoClass
