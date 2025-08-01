package org.careerseekers.userservice.dto.jwt

import org.careerseekers.userservice.dto.DtoClass

data class UserTokensDto(
    val accessToken: String,
    val refreshToken: String,
) : DtoClass
