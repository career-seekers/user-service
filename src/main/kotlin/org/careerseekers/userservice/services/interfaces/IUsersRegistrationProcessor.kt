package org.careerseekers.userservice.services.interfaces

import org.careerseekers.userservice.enums.UsersRoles

interface IUsersRegistrationProcessor<T> {
    val userRole: UsersRoles

    fun processRegistration(item: T)
}