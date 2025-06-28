@file:Suppress("UNCHECKED_CAST")

package org.careerseekers.userservice.io.converters

import org.careerseekers.userservice.io.BasicSuccessfulResponse

interface ConvertableToHttpResponse<T : ConvertableToHttpResponse<T>> {
    fun toHttpResponse(): BasicSuccessfulResponse<T> = BasicSuccessfulResponse(this as T)
}