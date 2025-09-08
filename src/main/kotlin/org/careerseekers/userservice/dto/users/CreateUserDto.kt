package org.careerseekers.userservice.dto.users

import org.careerseekers.userservice.dto.DtoClass
import org.careerseekers.userservice.enums.UsersRoles
import java.util.Date

data class CreateUserDto(
    val firstName: String,
    val lastName: String,
    val patronymic: String,
    val dateOfBirth: Date? = null,
    val email: String,
    val mobileNumber: String,
    var password: String? = null,
    val role: UsersRoles = UsersRoles.USER,
    val avatarId: Long? = null
) : DtoClass
