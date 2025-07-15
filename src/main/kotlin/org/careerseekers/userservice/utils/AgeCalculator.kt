package org.careerseekers.userservice.utils

import java.time.LocalDate
import java.time.Period

object AgeCalculator {
    fun getUserAge(birthday: LocalDate): Int? {
        val today = LocalDate.now()

        if (birthday.isAfter(today)) {
            return null
        }
        return Period.between(birthday, today).years
    }
}