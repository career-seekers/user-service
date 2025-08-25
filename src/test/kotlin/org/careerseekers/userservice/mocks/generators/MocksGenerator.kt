package org.careerseekers.userservice.mocks.generators

import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import kotlin.random.Random

object MocksGenerator {

    fun randomString(length: Int): String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    fun randomEmail(): String {
        return "${randomString(8)}@example.com"
    }

    fun randomDateOfBirth(): Date {
        val start = LocalDate.of(1950, 1, 1)
        val end = LocalDate.of(2005, 12, 31)
        val days = start.until(end).days
        val localDate = start.plusDays(Random.nextInt(days).toLong())

        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    fun randomBoolean(): Boolean = Random.nextBoolean()
}