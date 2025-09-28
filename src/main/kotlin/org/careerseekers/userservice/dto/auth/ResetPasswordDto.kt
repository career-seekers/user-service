package org.careerseekers.userservice.dto.auth

import org.careerseekers.userservice.dto.DtoClass

data class ResetPasswordDto(
    val email: String,
    val code: String,
    val newPassword: String,
    val confirmPassword: String,
) : DtoClass