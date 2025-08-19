package org.careerseekers.userservice.dto.users

import org.careerseekers.userservice.dto.DtoClass
import org.careerseekers.userservice.entities.Children
import org.careerseekers.userservice.entities.Users

data class CreateMentorToChildDto(
    val child: Children,
    val mentor: Users?
) : DtoClass
