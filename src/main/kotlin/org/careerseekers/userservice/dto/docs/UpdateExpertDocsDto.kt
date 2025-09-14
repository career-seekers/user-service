package org.careerseekers.userservice.dto.docs

import org.careerseekers.userservice.dto.DtoClass

data class UpdateExpertDocsDto(
    val id: Long,
    val institution: String?,
    val post: String?,
) : DtoClass
