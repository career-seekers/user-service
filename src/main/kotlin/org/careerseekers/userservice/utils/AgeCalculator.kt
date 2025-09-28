package org.careerseekers.userservice.utils

import java.time.LocalDate
import java.time.Period

object AgeCalculator {
    fun calculateAge(birthDate: LocalDate, currentDate: LocalDate): Int {
        val period = Period.between(birthDate, currentDate)
        return period.years
    }
}