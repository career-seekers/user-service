package org.careerseekers.userservice.io.converters.extensions

import com.google.protobuf.Timestamp
import java.util.Date

fun Timestamp.toDate(): Date {
    val millis = this.seconds * 1000 + this.nanos / 1_000_000
    return Date(millis)
}