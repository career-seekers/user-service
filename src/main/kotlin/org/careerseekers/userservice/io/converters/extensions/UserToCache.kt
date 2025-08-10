package org.careerseekers.userservice.io.converters.extensions

import org.careerseekers.userservice.dto.UsersCacheDto
import org.careerseekers.userservice.entities.Users

fun Users.toCache(): UsersCacheDto {
    return UsersCacheDto(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        patronymic = this.patronymic,
        dateOfBirth = this.dateOfBirth,
        email = this.email,
        mobileNumber = this.mobileNumber,
        password = this.password,
        role = this.role,
        avatarId = this.avatarId,
        verified = this.verified,
    )
}