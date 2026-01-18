package org.careerseekers.userservice.io.converters.extensions

import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.springframework.data.domain.Page

fun <T> Page<T>.toHttpResponse() = BasicSuccessfulResponse<Page<T>>(this)