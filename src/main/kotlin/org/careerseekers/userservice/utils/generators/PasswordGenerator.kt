package org.careerseekers.userservice.utils.generators

import kotlin.random.Random

object PasswordGenerator {
    private const val PASSWORD_LENGTH = 16
    private val CHAR_POOL: List<Char> = (
            ('a'..'z').toList() +
                    ('A'..'Z').toList() +
                    ('0'..'9').toList() +
                    "!@#\$%^&*()-_=+[]{}<>?".toList()
            )

    fun generatePassword(): String {
        return (1..PASSWORD_LENGTH)
            .map { Random.nextInt(0, CHAR_POOL.size) }
            .map(CHAR_POOL::get)
            .joinToString("")
    }
}