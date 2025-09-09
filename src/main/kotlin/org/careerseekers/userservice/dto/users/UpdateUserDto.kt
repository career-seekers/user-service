package org.careerseekers.userservice.dto.users

import org.careerseekers.userservice.dto.DtoClass

data class UpdateUserDto(
    val id: Long,
    val firstName: String? = null,
    val lastName: String? = null,
    val patronymic: String? = null,
    val email: String? = null,
    val mobileNumber: String? = null,
) : DtoClass