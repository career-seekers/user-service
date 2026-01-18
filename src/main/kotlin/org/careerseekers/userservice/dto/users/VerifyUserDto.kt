package org.careerseekers.userservice.dto.users

import org.careerseekers.userservice.dto.DtoClass
import org.careerseekers.userservice.enums.VerificationStatuses

data class VerifyUserDto(
    val userId: Long,
    val status: VerificationStatuses,
) : DtoClass
