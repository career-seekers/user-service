package org.careerseekers.userservice.dto.jwt

import org.careerseekers.userservice.dto.DtoClass
import org.careerseekers.userservice.entities.Users
import java.util.UUID

data class SaveRefreshTokenDto(
    val user: Users,
    val uuid: UUID,
    val token: String
) : DtoClass
