package org.careerseekers.userservice.utils

import org.careerseekers.userservice.exceptions.MobileNumberFormatException

object MobileNumberFormatter {
    fun checkMobileNumberValid(mobile: String) {
        val charsValid = mobile.all { it.isDigit() || it == '+'}

        if (mobile.length != 12 || !charsValid) {
            throw MobileNumberFormatException("Mobile number must be 12 characters long in format '+79991234567'")
        }
    }
}