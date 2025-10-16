package org.careerseekers.userservice.aspects

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.careerseekers.userservice.annotations.AccessUntil
import org.careerseekers.userservice.exceptions.AccessBlockedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Aspect
@Component
class AccessUntilAspect {

    @Before("@annotation(accessUntil)")
    fun checkAccessMethod(joinPoint: JoinPoint, accessUntil: AccessUntil) {
        processAccess(joinPoint, accessUntil)
    }

    @Before("@within(accessUntil)")
    fun checkAccessClass(joinPoint: JoinPoint, accessUntil: AccessUntil) {
        processAccess(joinPoint, accessUntil)
    }

    private fun processAccess(joinPoint: JoinPoint, accessUntil: AccessUntil) {
        if (accessUntil.allowedRoles.any { hasRole(it.toString()) }) {
            return
        }

        val formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
        val limitDate = ZonedDateTime.parse(accessUntil.until, formatter)
        val now = ZonedDateTime.now()

        if (now.isAfter(limitDate)) {
            throw AccessBlockedException(accessUntil.errorMessage.ifBlank { "Доступ к этому методу закрыт с $limitDate" })
        }
    }

    private fun hasRole(role: String): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication == null || !authentication.isAuthenticated) {
            return false
        }

        return authentication.authorities.any { it.authority == role }
    }
}