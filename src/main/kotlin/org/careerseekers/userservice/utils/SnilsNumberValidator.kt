package org.careerseekers.userservice.utils

import org.careerseekers.userservice.exceptions.BadRequestException

/**
 * Validator for SNILS number.
 *
 * Checks that the SNILS number consists exactly of 11 digits.
 * Throws [BadRequestException] with a descriptive message if validation fails.
 */
object SnilsNumberValidator {

    /**
     * Local version of the require function that checks a condition and
     * throws [BadRequestException] if the condition is false.
     *
     * @param value the condition to check
     * @param lazyMessage lambda that supplies the error message
     * @throws BadRequestException if the condition is not met
     */
    private fun require(value: Boolean, lazyMessage: () -> Any) {
        if (!value) {
            throw BadRequestException(lazyMessage().toString())
        }
    }

    /**
     * Validates the given SNILS number.
     *
     * Validation rules:
     * - The string length must be exactly 11 characters.
     * - All characters must be digits.
     *
     * @param snilsNumber the SNILS number string to validate
     * @return `true` if the SNILS number is valid
     * @throws BadRequestException if validation fails
     */
    fun validateSnils(snilsNumber: String): Boolean {
        require(snilsNumber.length == 11) { "Snils number length must be 11 chars" }
        require(snilsNumber.all { it.isDigit() }) { "Snils number must be a digit" }
        return true
    }
}
