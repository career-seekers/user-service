package org.careerseekers.userservice.dto.docs

import org.careerseekers.userservice.dto.DtoClass

data class CreateExpertDocsTransferDto(
    val userId: Long,
    val institution: String,
    val post: String,
    val consentToExpertPdpId: Long?,
) : DtoClass