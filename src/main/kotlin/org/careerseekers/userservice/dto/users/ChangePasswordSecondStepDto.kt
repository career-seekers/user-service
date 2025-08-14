package org.careerseekers.userservice.dto.users

import org.careerseekers.userservice.dto.DtoClass

data class ChangePasswordSecondStepDto(
    val verificationCode: String,
    val newPassword: String,
) : DtoClass