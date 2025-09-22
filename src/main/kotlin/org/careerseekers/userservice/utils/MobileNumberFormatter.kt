package org.careerseekers.userservice.utils

import org.careerseekers.userservice.exceptions.MobileNumberFormatException

object MobileNumberFormatter {
    fun checkMobileNumberValid(mobile: String) {
        val charsValid = mobile.startsWith('+') && mobile.substring(1).all { it.isDigit() }

        if (mobile.length != 12 || !charsValid) {
            throw MobileNumberFormatException("Номер телефона должен быть 12 символов в длину в формате '+79991234567'.")
        }
    }
}