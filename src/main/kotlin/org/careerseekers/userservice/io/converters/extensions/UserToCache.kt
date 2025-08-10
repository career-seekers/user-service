package org.careerseekers.userservice.io.converters.extensions

import org.careerseekers.userservice.dto.cache.UsersCacheDto
import org.careerseekers.userservice.entities.Users

fun Users.toCache(): UsersCacheDto {
    return UsersCacheDto(
        id = this.id
    )
}