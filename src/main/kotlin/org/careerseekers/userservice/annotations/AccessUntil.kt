package org.careerseekers.userservice.annotations

import org.careerseekers.userservice.enums.UsersRoles


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class AccessUntil(
    val until: String,
    val allowedRoles: Array<UsersRoles> = [],
    val errorMessage: String = "",
)
