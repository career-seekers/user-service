package org.careerseekers.userservice.dto.users

import org.careerseekers.userservice.dto.DtoClass
import org.careerseekers.userservice.entities.Users
import java.util.Date

data class CreateChildDto(
    val lastName: String,
    val firstName: String,
    val patronymic: String,
    val dateOfBirth: Date,
    val createdAt: Date = Date(),
    val userId: Long,
    val mentorId: Long?,
    var user: Users? = null,
    var mentor: Users? = null,
) : DtoClass