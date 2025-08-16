package org.careerseekers.userservice.dto.auth

import org.careerseekers.userservice.dto.DtoClass
import org.careerseekers.userservice.enums.UsersRoles
import java.util.Date
import java.util.UUID

data class RegisterUserDto(
    val firstName: String,
    val lastName: String,
    val patronymic: String,
    val dateOfBirth: Date,
    val email: String,
    val mobileNumber: String,
    val password: String?,
    val role: UsersRoles,
    val avatarId: Long?,
    val uuid: UUID,
    val mentorEqualsUser: Boolean? = null,
    val mentorId: Long? = null,
) : DtoClass