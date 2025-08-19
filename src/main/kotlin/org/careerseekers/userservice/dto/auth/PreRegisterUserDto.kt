package org.careerseekers.userservice.dto.auth

import org.careerseekers.userservice.dto.DtoClass

data class PreRegisterUserDto(
    val email: String,
    val mobileNumber: String,
) : DtoClass
