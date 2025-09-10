package org.careerseekers.userservice.mocks.generators

import org.careerseekers.userservice.mocks.generators.MocksGenerator.randomBoolean
import org.careerseekers.userservice.mocks.generators.MocksGenerator.randomDateOfBirth
import org.careerseekers.userservice.mocks.generators.MocksGenerator.randomEmail
import org.careerseekers.userservice.mocks.generators.MocksGenerator.randomString
import org.careerseekers.userservice.dto.auth.UserRegistrationDto
import org.careerseekers.userservice.dto.auth.UserWithChildRegistrationDto
import org.careerseekers.userservice.enums.UsersRoles
import java.util.UUID
import kotlin.random.Random

object RegistrationsDtoGenerator {

    fun createUserRegistrationDto(role: UsersRoles) = UserRegistrationDto(
        verificationCode = (100000..999999).random().toString(),
        firstName = randomString(6),
        lastName = randomString(6),
        patronymic = randomString(6),
        dateOfBirth = randomDateOfBirth(),
        email = randomEmail(),
        mobileNumber = "+7" + (1000000000..9999999999).random().toString(),
        password = randomString(12),
        role = role,
        uuid = UUID.randomUUID()
    )

    fun createUserWithChildRegistrationDto(role: UsersRoles) = UserWithChildRegistrationDto(
        verificationCode = (100000..999999).random().toString(),
        firstName = randomString(6),
        lastName = randomString(6),
        patronymic = randomString(6),
        dateOfBirth = randomDateOfBirth(),
        email = randomEmail(),
        mobileNumber = "+7" + (1000000000..9999999999).random().toString(),
        password = randomString(12),
        role = role,
        uuid = UUID.randomUUID(),
        mentorEqualsUser = randomBoolean(),
        childFirstName = randomString(6),
        childLastName = randomString(6),
        childPatronymic = randomString(6),
        childDateOfBirth = randomDateOfBirth(),
        mentorId = Random.nextLong(1, 100)
    )
}