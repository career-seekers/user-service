package org.careerseekers.userservice.dto.auth

import org.careerseekers.userservice.dto.DtoClass

data class CodeVerificationDto(
    val code: String,
    val email: String,
) : DtoClass
