package org.careerseekers.userservice.dto.docs

import org.careerseekers.userservice.dto.DtoClass
import org.careerseekers.userservice.entities.Users

data class CreateExpertDocsDto(
    val userId: Long,
    val institution: String,
    val post: String,
    var user: Users? = null,
) : DtoClass
