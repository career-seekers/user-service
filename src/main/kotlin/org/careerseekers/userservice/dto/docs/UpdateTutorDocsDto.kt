package org.careerseekers.userservice.dto.docs

import org.careerseekers.userservice.dto.DtoClass
import org.springframework.web.multipart.MultipartFile

data class UpdateTutorDocsDto(
    val id: Long,
    val institution: String?,
    val post: String?,
    val consentToTutorPdp: MultipartFile?,
) : DtoClass
