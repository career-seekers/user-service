package org.careerseekers.userservice.dto.users

import org.careerseekers.userservice.dto.DtoClass
import org.careerseekers.userservice.enums.UsersRoles
import java.util.Date

data class CreateUserDto(
    val firstName: String,
    val lastName: String,
    val patronymic: String,
    val dateOfBirth: Date,
    val email: String,
    val mobileNumber: String,
    val password: String,
    val role: UsersRoles = UsersRoles.USER,
    val avatarId: Long? = null
) : DtoClass
