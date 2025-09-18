package org.careerseekers.userservice.dto.auth

import org.careerseekers.userservice.dto.DtoClass
import java.util.UUID

data class LoginUserDto(
    var email: String,
    val password: String,
    val uuid: UUID,
) : DtoClass