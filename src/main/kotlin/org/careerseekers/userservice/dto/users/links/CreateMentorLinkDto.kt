package org.careerseekers.userservice.dto.users.links

import org.careerseekers.userservice.dto.DtoClass
import org.careerseekers.userservice.entities.Users

data class CreateMentorLinkDto(
    val userId: Long,
    var biscuit: String?,
    var user: Users?
) : DtoClass