package org.careerseekers.userservice.io.converters.extensions

import org.careerseekers.userservice.exceptions.InvalidNumberFormatException

fun String.toLongOrThrow(message: String = "Недопустимое значение для класса Long: '$this'"): Long =
    this.toLongOrNull() ?: throw InvalidNumberFormatException(message)

fun String.toShortOrThrow(message: String = "Недопустимое значение для класса Short: '$this'"): Short =
    this.toShortOrNull() ?: throw InvalidNumberFormatException(message)
