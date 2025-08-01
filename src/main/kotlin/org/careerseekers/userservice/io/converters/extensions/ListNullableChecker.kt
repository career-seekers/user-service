package org.careerseekers.userservice.io.converters.extensions

import org.careerseekers.userservice.exceptions.BadRequestException

/**
 * Extension function for a [List] of nullable elements.
 *
 * Checks that all elements in the list are either all `null` or all non-null.
 * If the list contains a mix of `null` and non-null values, a [BadRequestException] is thrown.
 *
 * @throws BadRequestException if the list contains a mixture of null and non-null elements.
 */
fun <T> List<T?>.checkNullable(message: String = "These parameters can be only all null values or all non-null values.") {
    val nullCount = this.count { it == null }
    if (nullCount > 0 && nullCount < this.size) {
        throw BadRequestException(message)
    }
}