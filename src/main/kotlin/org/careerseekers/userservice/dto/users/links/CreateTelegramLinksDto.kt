package org.careerseekers.userservice.dto.users.links

import org.careerseekers.userservice.dto.DtoClass
import org.careerseekers.userservice.entities.Users

data class CreateTelegramLinksDto(
    val tgLink: String,
    val userId: Long,
    var user: Users? = null,
) : DtoClass