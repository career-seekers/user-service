package org.careerseekers.userservice.dto.users

import org.careerseekers.userservice.dto.DtoClass

data class UpdateUserDto(
    val id: Long,
    val firstName: String?,
    val lastName: String?,
    val patronymic: String?,
    val password: String?,
) : DtoClass