package org.careerseekers.userservice.services.processors

import org.careerseekers.userservice.dto.auth.RegistrationDto
import org.careerseekers.userservice.enums.UsersRoles

interface IUsersRegistrationProcessor {
    val userRole: UsersRoles

    fun <T : RegistrationDto> processRegistration(item: T)
}