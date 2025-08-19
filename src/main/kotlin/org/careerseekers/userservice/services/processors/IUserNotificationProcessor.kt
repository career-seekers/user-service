package org.careerseekers.userservice.services.processors

import org.careerseekers.userservice.entities.Users

interface IUserNotificationProcessor {
    fun notifyUser(user: Users)
}