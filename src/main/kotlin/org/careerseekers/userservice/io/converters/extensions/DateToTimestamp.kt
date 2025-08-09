package org.careerseekers.userservice.io.converters.extensions

import com.google.protobuf.Timestamp
import java.util.Date

fun Date.toTimestamp(): Timestamp {
    val seconds = this.time / 1000
    val nanos = ((this.time % 1000) * 1_000_000).toInt()
    return Timestamp.newBuilder()
        .setSeconds(seconds)
        .setNanos(nanos)
        .build()
}