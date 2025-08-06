package org.careerseekers.userservice.dto.docs

import org.careerseekers.userservice.dto.DtoClass
import org.careerseekers.userservice.entities.Users

data class CreateTutorDocsTransferDto(
    val user: Users,
    val institution: String,
    val post: String,
    val consentToTutorPdpId: Long?,
) : DtoClass
