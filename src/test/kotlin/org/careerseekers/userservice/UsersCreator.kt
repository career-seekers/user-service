package org.careerseekers.userservice

import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.enums.UsersRoles
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import kotlin.random.Random

object UsersCreator {
    private val roles =
        listOf(UsersRoles.USER, UsersRoles.EXPERT, UsersRoles.MENTOR, UsersRoles.TUTOR, UsersRoles.ADMIN)

    private fun randomString(length: Int): String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    private fun randomEmail(): String {
        return "${randomString(8)}@example.com"
    }

    private fun randomDateOfBirth(): Date {
        val start = LocalDate.of(1950, 1, 1)
        val end = LocalDate.of(2005, 12, 31)
        val days = start.until(end).days
        val localDate = start.plusDays(Random.nextInt(days).toLong())

        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    private fun randomBoolean(): Boolean = Random.nextBoolean()

    fun createUser(): Users {
        return Users(
            id = Random.nextLong(1, 10000),
            firstName = randomString(6),
            lastName = randomString(8),
            patronymic = randomString(7),
            dateOfBirth = randomDateOfBirth(),
            email = randomEmail(),
            mobileNumber = "+7" + (1000000000..9999999999).random().toString(),
            password = randomString(12),
            role = roles.random(),
            avatarId = Random.nextLong(1, 100),
            verified = randomBoolean(),
            isMentor = randomBoolean(),
            jwtTokens = null,           
            userDocuments = null,       
            expertDocuments = null,     
            tutorDocuments = null,      
            mentorDocuments = null,     
            menteeChildren = null,      
            children = null             
        )
    }
}