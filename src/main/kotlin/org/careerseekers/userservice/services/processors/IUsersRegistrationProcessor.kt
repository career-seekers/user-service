package org.careerseekers.userservice.services.processors

import org.careerseekers.userservice.dto.auth.UserRegistrationDto
import org.careerseekers.userservice.enums.UsersRoles

interface IUsersRegistrationProcessor {
    val userRole: UsersRoles

    fun processRegistration(item: UserRegistrationDto)
}