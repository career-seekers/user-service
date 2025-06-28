package org.careerseekers.userservice.io.converters

import org.careerseekers.userservice.io.BasicSuccessfulResponse

fun String.toHttpResponse(): BasicSuccessfulResponse<String> {
    return BasicSuccessfulResponse(this)
}