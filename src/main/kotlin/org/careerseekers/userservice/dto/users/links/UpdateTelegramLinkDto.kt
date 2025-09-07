package org.careerseekers.userservice.dto.users.links

import org.careerseekers.userservice.dto.DtoClass

data class UpdateTelegramLinkDto(
    val id: Long,
    val tgLink: String,
) : DtoClass