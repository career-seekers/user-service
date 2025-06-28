package org.careerseekers.userservice.io.converters

import org.careerseekers.userservice.io.BasicSuccessfulResponse

fun <T> List<T>.toHttpResponse(): BasicSuccessfulResponse<List<T>> {
    return BasicSuccessfulResponse(this)
}