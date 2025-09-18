package org.careerseekers.userservice.dto.auth

import org.careerseekers.userservice.dto.DtoClass

data class PreRegisterUserDto(
    var email: String,
    val mobileNumber: String,
) : DtoClass
