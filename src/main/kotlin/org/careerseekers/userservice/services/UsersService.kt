package org.careerseekers.userservice.services

import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.Period

@Service
class UsersService {
    fun getUserAge(birthday: LocalDate): Int? {
        val today = LocalDate.now()

        if (birthday.isAfter(today)) {
            return null
        }
        return Period.between(birthday, today).years
    }
}