package org.careerseekers.userservice

import MocksGenerator.randomBoolean
import MocksGenerator.randomDateOfBirth
import MocksGenerator.randomEmail
import MocksGenerator.randomString
import org.careerseekers.userservice.dto.users.CreateUserDto
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.enums.UsersRoles
import kotlin.random.Random

object UsersCreator {
    private val roles =
        listOf(UsersRoles.USER, UsersRoles.EXPERT, UsersRoles.MENTOR, UsersRoles.TUTOR, UsersRoles.ADMIN)

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

    fun createUserDto(): CreateUserDto {
        val user = createUser()
        return CreateUserDto(
            firstName = user.firstName,
            lastName = user.lastName,
            patronymic = user.patronymic,
            dateOfBirth = user.dateOfBirth,
            email = user.email,
            mobileNumber = user.mobileNumber,
            password = user.password,
            role = user.role,
            avatarId = user.avatarId
        )
    }
}