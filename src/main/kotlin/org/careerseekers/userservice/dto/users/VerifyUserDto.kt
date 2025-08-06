package org.careerseekers.userservice.dto.users

import org.careerseekers.userservice.dto.DtoClass

data class VerifyUserDto(
    val userId: Long,
    val status: Boolean,
) : DtoClass
