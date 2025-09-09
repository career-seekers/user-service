package org.careerseekers.userservice.dto.users

import org.careerseekers.userservice.dto.DtoClass
import java.util.Date

data class UpdateUserDto(
    val id: Long,
    val firstName: String? = null,
    val lastName: String? = null,
    val patronymic: String? = null,
    val email: String? = null,
    val mobileNumber: String? = null,
    val dateOfBirth: Date? = null,
) : DtoClass