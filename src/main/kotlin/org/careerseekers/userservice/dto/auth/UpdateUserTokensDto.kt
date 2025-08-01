package org.careerseekers.userservice.dto.auth

import java.util.UUID

data class UpdateUserTokensDto(
    val accessToken: String,
    val refreshToken: String,
    val uuid: UUID,
)
