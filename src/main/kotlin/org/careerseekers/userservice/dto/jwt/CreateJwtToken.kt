package org.careerseekers.userservice.dto.jwt

import org.careerseekers.userservice.dto.DtoClass
import org.careerseekers.userservice.entities.Users
import java.util.UUID

data class CreateJwtToken(
    val user: Users,
    val uuid: UUID,
) : DtoClass