package org.careerseekers.userservice.services.interfaces

import org.careerseekers.userservice.entities.Users

interface IUserNotificationProcessor {
    fun notifyUser(user: Users)
}