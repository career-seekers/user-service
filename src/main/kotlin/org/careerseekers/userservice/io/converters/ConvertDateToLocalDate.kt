package org.careerseekers.userservice.io.converters

import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

fun convertDateToLocalDate(date: Date): LocalDate {
    return when (date) {
        is java.sql.Date -> date.toLocalDate()
        else -> date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }
}