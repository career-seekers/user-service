package org.careerseekers.userservice.dto.users

import org.careerseekers.userservice.dto.DtoClass

data class ChangePasswordSecondStepDto(
    val jwtToken: String,
    val verificationCode: String,
    val newPassword: String,
) : DtoClass