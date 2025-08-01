package org.careerseekers.userservice.io.converters.extensions

import org.careerseekers.userservice.exceptions.InvalidNumberFormatException

fun String.toLongOrThrow(message: String = "Invalid long value '$this'"): Long =
    this.toLongOrNull() ?: throw InvalidNumberFormatException(message)

fun String.toShortOrThrow(message: String = "Invalid short value '$this'"): Short =
    this.toShortOrNull() ?: throw InvalidNumberFormatException(message)
