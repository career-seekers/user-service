package org.careerseekers.userservice.dto.auth

import org.careerseekers.userservice.dto.DtoClass
import org.careerseekers.userservice.enums.UsersRoles
import java.util.Date
import java.util.UUID

data class UserRegistrationDto(
    val verificationCode: String,
    val firstName: String,
    val lastName: String,
    val patronymic: String? = null,
    val dateOfBirth: Date? = null,
    val email: String,
    val mobileNumber: String,
    val password: String? = null,
    val role: UsersRoles,
    val uuid: UUID,
    val mentorEqualsUser: Boolean? = false,
) : DtoClass