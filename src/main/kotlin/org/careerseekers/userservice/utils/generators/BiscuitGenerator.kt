package org.careerseekers.userservice.utils.generators

/**
 * Object for generating unique token strings ("biscuits") for public invitation links.
 *
 * The main functionality is to create a random alphanumeric string consisting of
 * uppercase letters, lowercase letters, and digits.
 *
 * @property length The length of the generated token string, default is 16 characters.
 *
 * @return A randomly generated alphanumeric string of the specified length.
 */
object BiscuitGenerator {

    fun generateLinkBiscuit(length: Int = 16): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}