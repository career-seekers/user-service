package org.careerseekers.userservice.utils

import org.careerseekers.userservice.exceptions.BadRequestException
import org.careerseekers.userservice.exceptions.DoubleRecordException
import org.careerseekers.userservice.repositories.UserDocsRepository


/**
 * Component for validating SNILS.
 *
 * This validator performs two main checks:
 * 1. Structural validation: Ensures the SNILS number adheres to basic format rules (length and digit-only characters).
 * 2. Uniqueness validation: Checks against the [UserDocsRepository] to prevent duplicate SNILS numbers
 *    from being registered if they already exist in the system.
 *
 * @param userDocsRepository The repository used to check for existing SNILS numbers.
 */
@Utility
class SnilsValidator(private val userDocsRepository: UserDocsRepository) {

    /**
     * Local version of the `require` function.
     *
     * This utility function checks a given condition. If the condition evaluates to `false`,
     * it throws a [BadRequestException] with a provided lazy message. This allows
     * custom exception types to be thrown instead of the default `IllegalArgumentException`
     * used by Kotlin's standard `require`.
     *
     * @param value the boolean condition to check.
     * @param lazyMessage a lambda function that returns the error message to be used if the condition is false.
     *                    This message is only evaluated if the condition fails.
     * @throws BadRequestException if `value` is `false`.
     */
    private fun require(value: Boolean, lazyMessage: () -> Any) {
        if (!value) {
            throw BadRequestException(lazyMessage().toString())
        }
    }

    /**
     * Validates the structural format of a SNILS number.
     *
     * This method performs the following checks:
     * - The string length must be exactly 11 characters.
     * - All characters in the string must be digits (0-9).
     *
     * If any of these conditions are not met, a [BadRequestException] is thrown.
     *
     * @param snilsNumber The SNILS number string to be validated.
     * @return `true` if the SNILS number passes all structural validation rules.
     * @throws BadRequestException if the SNILS number's format is invalid.
     */
    fun validateSnilsNumber(snilsNumber: String): Boolean {
        require(snilsNumber.length == 11) { "Snils number length must be 11 chars" }
        require(snilsNumber.all { it.isDigit() }) { "Snils number must be a digit" }
        return true
    }

    /**
     * Performs a comprehensive validation of a SNILS number, including structural and uniqueness checks.
     *
     * First, it calls [validateSnilsNumber] to ensure the SNILS format is correct.
     * Second, it queries the [UserDocsRepository] to check if a document with the
     * provided SNILS number already exists in the system.
     *
     * @param snilsNumber The SNILS number string to be checked.
     * @throws BadRequestException if the SNILS number's format is invalid (from [validateSnilsNumber]).
     * @throws DoubleRecordException if a document with the given SNILS number already exists in the database.
     */
    fun checkSnilsValid(snilsNumber: String) {
        validateSnilsNumber(snilsNumber)

        userDocsRepository.findBySnilsNumber(snilsNumber)?.let {
            throw DoubleRecordException("Documents with snils number $snilsNumber already exists")
        }
    }
}
