package org.careerseekers.userservice.dto.users

import org.careerseekers.userservice.dto.DtoClass
import java.util.Date

data class UpdateChildDto(
    val id: Long,
    val lastName: String?,
    val firstName: String?,
    val patronymic: String?,
    val dateOfBirth: Date?,
    val mentorId: Long?,
) : DtoClass